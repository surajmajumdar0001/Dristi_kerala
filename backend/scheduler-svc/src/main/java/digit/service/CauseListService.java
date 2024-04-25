package digit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.kafka.Producer;
import digit.repository.CauseListRepository;
import digit.repository.HearingRepository;
import digit.util.MdmsUtil;
import digit.util.PdfServiceUtil;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class CauseListService {

    private HearingRepository hearingRepository;

    private CauseListRepository causeListRepository;

    private Producer producer;

    private Configuration config;

    private PdfServiceUtil pdfServiceUtil;

    private MdmsUtil mdmsUtil;

    private ServiceConstants serviceConstants;

    @Autowired
    public CauseListService(HearingRepository hearingRepository, CauseListRepository causeListRepository,
                            Producer producer, Configuration config, PdfServiceUtil pdfServiceUtil,
                            MdmsUtil mdmsUtil) {
        this.hearingRepository = hearingRepository;
        this.causeListRepository = causeListRepository;
        this.producer = producer;
        this.config =  config;
        this.pdfServiceUtil = pdfServiceUtil;
        this.mdmsUtil = mdmsUtil;
    }

    public void updateCauseListForTomorrow() {
        log.info("operation = updateCauseListForTomorrow, result = IN_PROGRESS");
        List<CauseList> causeLists = new ArrayList<>();
        //TODO get judges from db once tables are ready
        List<String> judgeIds = new ArrayList<>();

        // Multi Thread processing: process 10 judges at a time
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (String judgeId : judgeIds) {
            // Submit a task to the executor service for each judge
            executorService.submit(() -> generateCauseListForJudge(judgeId, causeLists));
        }
        CauseListResponse causeListResponse = CauseListResponse.builder()
                .responseInfo(ResponseInfo.builder().build()).causeList(causeLists).build();
        producer.push(config.getCauseListInsertTopic(), causeListResponse);
        log.info("operation = updateCauseListForTomorrow, result = SUCCESS");
    }

    private void generateCauseListForJudge(String judgeId, List<CauseList> causeLists) {
        log.info("operation = generateCauseListForJudge, result = IN_PROGRESS, judgeId = {}", judgeId);
        HearingSearchCriteria searchCriteria =  HearingSearchCriteria.builder()
                .judgeId(judgeId)
                .fromDate(LocalDate.now().plusDays(1))
                .toDate(LocalDate.now().plusDays(1))
                .build();
        List<ScheduleHearing> scheduleHearings = hearingRepository.getHearings(searchCriteria);
        if (CollectionUtils.isEmpty(scheduleHearings)) {
            log.info("No hearings scheduled tomorrow for judgeId = {}", judgeId);
        } else {
            fillHearingTimesWithDataFromMdms(scheduleHearings);
            generateCauseListFromHearings(scheduleHearings, causeLists);
            log.info("operation = generateCauseListForJudge, result = SUCCESS, judgeId = {}", judgeId);
        }
    }

    private void fillHearingTimesWithDataFromMdms(List<ScheduleHearing> scheduleHearings) {
        log.info("operation = fillHearingTimesWithDataFromMdms, result = IN_PROGRESS, judgeId = {}", scheduleHearings.get(0).getJudgeId());
        RequestInfo requestInfo = new RequestInfo();
        //TODO finalise on tenant id and create a system user for calls without proper request info
        Map<String, Map<String, JSONArray>> defaultHearingsData =
                mdmsUtil.fetchMdmsData(requestInfo, "kl", serviceConstants.DEFAULT_COURT_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_HEARING_MASTER_NAME));
        JSONArray jsonArray = defaultHearingsData.get("court").get("hearings");
        Map<String, Integer> hearingTypeToTimeInMinutesMap = createHearingTypeToTimeMap(jsonArray);
        for (ScheduleHearing scheduleHearing : scheduleHearings) {
            Integer hearingTimeInMinutes = hearingTypeToTimeInMinutesMap.get(scheduleHearing.getEventType().toString());
            if (hearingTimeInMinutes != null) {
                scheduleHearing.setHearingTimeInMinutes(hearingTimeInMinutes);
            }
        }
        log.info("operation = fillHearingTimesWithDataFromMdms, result = SUCCESS, judgeId = {}", scheduleHearings.get(0).getJudgeId());
    }

    private Map<String, Integer> createHearingTypeToTimeMap(JSONArray jsonArray) {
        log.info("operation = createHearingTypeToTimeMap, result = IN_PROGRESS");
        Map<String, Integer> hearingTypeToTimeInMinutesMap = new HashMap<>();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            String hearingType = (String) jsonObject.get("hearingType");
            int hearingTimeInMinutes = Integer.parseInt((String) jsonObject.get("hearingTime"));
            hearingTypeToTimeInMinutesMap.put(hearingType, hearingTimeInMinutes);
        }
        log.info("operation = createHearingTypeToTimeMap, result = SUCCESS");
        return hearingTypeToTimeInMinutesMap;
    }

    private void generateCauseListFromHearings(List<ScheduleHearing> scheduleHearings, List<CauseList> causeLists) {
        log.info("operation = generateCauseListFromHearings, result = SUCCESS, judgeId = {}", scheduleHearings.get(0).getJudgeId());
        List<Slot> slotList = getSlottingDataFromMdms();
        scheduleHearings.sort(Comparator.comparing(ScheduleHearing::getEventType));
        int currentSlotIndex = 0; // Track the current slot index
        int accumulatedTime = 0; // Track accumulated hearing time within the slot

        for (ScheduleHearing hearing : scheduleHearings) {
            while (currentSlotIndex < slotList.size()) {
                Slot slot = slotList.get(currentSlotIndex);
                int hearingTime = hearing.getHearingTimeInMinutes();

                if (accumulatedTime + hearingTime <= slot.getSlotDuration()) {
                    CauseList causeList = getCauseListFromHearingAndSlot(hearing, slot);
                    causeLists.add(causeList);
                    accumulatedTime += hearingTime;
                    break; // Move to the next hearing
                } else {
                    // Move to the next slot
                    currentSlotIndex++;
                    accumulatedTime = 0; // Reset accumulated time for the new slot
                }
            }

            if (currentSlotIndex == slotList.size()) {
                // Add remaining cases to the last slot
                Slot lastSlot = slotList.get(slotList.size() - 1);
                CauseList causeList = getCauseListFromHearingAndSlot(hearing, lastSlot);
                causeLists.add(causeList);
            }
        }
        log.info("operation = generateCauseListFromHearings, result = SUCCESS, judgeId = {}", scheduleHearings.get(0).getJudgeId());
    }

    private static CauseList getCauseListFromHearingAndSlot(ScheduleHearing hearing, Slot slot) {
        return CauseList.builder()
                .judgeId(hearing.getJudgeId())
                .courtId(hearing.getCourtId())
                .caseId(hearing.getCaseId())
                .typeOfHearing(hearing.getEventType().name())
                .tentativeSlot(slot.getSlotName())
                .caseDate(hearing.getDate().toString())
                .caseTitle(hearing.getTitle())
                .build();
    }


    private List<Slot> getSlottingDataFromMdms() {
        log.info("operation = getSlottingDataFromMdms, result = IN_PROGRESS");
        RequestInfo requestInfo = new RequestInfo();
        Map<String, Map<String, JSONArray>> defaultHearingsData =
                mdmsUtil.fetchMdmsData(requestInfo, "kl", serviceConstants.DEFAULT_COURT_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_SLOTTING_MASTER_NAME));
        JSONArray jsonArray = defaultHearingsData.get("court").get("slots");
        List<Slot> slots = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            for (Object obj : jsonArray) {
                Slot slot = objectMapper.readValue(obj.toString(), Slot.class);
                slots.add(slot);
            }
        } catch (JsonProcessingException e) {
            log.error("Error occurred when reading slotting data from mdms" + e.getMessage());
            throw new CustomException("DK_SL_APP_ERR", "Error occurred when reading slotting data from mdms");
        }
        log.info("operation = getSlottingDataFromMdms, result = SUCCESS");
        return slots;
    }

    public List<CauseList> viewCauseListForTomorrow(CauseListSearchRequest searchRequest) {
        log.info("operation = viewCauseListForTomorrow, with searchRequest : {}", searchRequest.toString());
        return getCauseListForTomorrow(searchRequest);
    }

    private List<CauseList> getCauseListForTomorrow(CauseListSearchRequest searchRequest) {
        return causeListRepository.getCauseLists(searchRequest.getCauseListSearchCriteria());
    }

    public ByteArrayResource downloadCauseListForTomorrow(CauseListSearchRequest searchRequest) {
        log.info("operation = downloadCauseListForTomorrow, with searchRequest : {}", searchRequest.toString());
        List<CauseList> causeLists = getCauseListForTomorrow(searchRequest);
        CauseListResponse causeListResponse = CauseListResponse.builder()
                .responseInfo(ResponseInfo.builder().build()).causeList(causeLists).build();
        return pdfServiceUtil.generatePdfFromPdfService(causeListResponse , searchRequest.getRequestInfo().getUserInfo().getTenantId(),
                config.getCauseListPdfTemplateKey(), searchRequest.getRequestInfo());
    }
}

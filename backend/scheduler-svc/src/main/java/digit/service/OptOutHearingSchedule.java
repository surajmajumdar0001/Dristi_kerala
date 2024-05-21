package digit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.kafka.Producer;
import digit.repository.ReScheduleRequestRepository;
import digit.web.models.*;
import digit.web.models.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class OptOutHearingSchedule {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    HearingService hearingService;

    @Autowired
    private ReScheduleRequestRepository repository;

    @Autowired
    private Producer producer;

    @Autowired
    private Configuration configuration;

    public void checkAndScheduleHearingForOptOut(HashMap<String, Object> record) {


        try {
            OptOutRequest optOutRequest = mapper.convertValue(record, OptOutRequest.class);
            RequestInfo requestInfo = optOutRequest.getRequestInfo();

            List<OptOut> optOuts = optOutRequest.getOptOuts();

            optOuts.forEach((optOut -> {


                // get the list and cancelled the hearings
                List<ScheduleHearing> hearingList = hearingService.search(HearingSearchRequest
                        .builder().requestInfo(requestInfo)
                        .criteria(HearingSearchCriteria.builder()
                                .rescheduleId(optOut.getRescheduleRequestId())
                                .status(Collections.singletonList(Status.BLOCKED)).build()).build());

                hearingList.forEach(hearing -> hearing.setStatus(Status.CANCELLED));

                //release judge calendar
                hearingService.update(ScheduleHearingRequest.builder()
                        .requestInfo(requestInfo)
                        .hearing(hearingList).build());


                //TODO: get list of litigants
                // for now fetching mdms dummy case and checking all the litigants for the case

//                Map<String, Map<String, JSONArray>> mdmsCase = mdmsUtil.fetchMdmsData(RequestInfo.builder().build(), "kl", "schedule-hearing", Collections.singletonList("cases"));
//
//                LinkedHashMap map = (LinkedHashMap) mdmsCase.get("schedule-hearing").get("cases").get(0);
//
//                ArrayList representatives = (ArrayList) map.get("representatives");
//                List<String> ids = new ArrayList<>();
//                for (Object representative : representatives) {
//                    LinkedHashMap element = (LinkedHashMap) representative;
//                    String id = (String) element.get("advocateId");
//                    ids.add(id);
//                }


                //TODO: get opt out of litigants

//                List<OptOut> optOutForRescheduleRequest = optOutService.search(OptOutSearchRequest.builder().requestInfo(RequestInfo.builder().build()).criteria(
//                        OptOutSearchCriteria.builder()
//                                .rescheduleRequestId(optOut.getRescheduleRequestId()).build()
//                ).build());


                //if this is last one then update the status to review

                //  updated available days in db
                String rescheduleRequestId = optOut.getRescheduleRequestId();

                List<ReScheduleHearing> reScheduleRequest = repository.getReScheduleRequest(ReScheduleHearingReqSearchCriteria.builder()
                        .rescheduledRequestId(Collections.singletonList(rescheduleRequestId)).build());


                List<LocalDate> suggestedDates = reScheduleRequest.get(0).getSuggestedDates();
                Set<LocalDate> suggestedDatesSet = new HashSet<>(suggestedDates);

                optOut.getOptoutDates().forEach(suggestedDatesSet::remove);


                reScheduleRequest.get(0).setAvailableDates(new ArrayList<>(suggestedDatesSet));
                reScheduleRequest.get(0).setStatus(Status.REVIEW);


                producer.push(configuration.getUpdateRescheduleRequestTopic(), reScheduleRequest);

            }));
        } catch (Exception e) {
            log.error("KAFKA_PROCESS_ERROR:", e);
        }
    }
}

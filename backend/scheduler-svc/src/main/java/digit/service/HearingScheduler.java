package digit.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.helper.DefaultMasterDataHelper;
import digit.kafka.Producer;
import digit.repository.ReScheduleRequestRepository;
import digit.web.models.*;
import digit.web.models.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HearingScheduler {

    private final Producer producer;

    private final ReScheduleRequestRepository repository;

    private final Configuration configuration;

    private final ObjectMapper mapper;

    private final CalendarService calendarService;

    private final HearingService hearingService;

    private final DefaultMasterDataHelper helper;

    private final ServiceConstants serviceConstants;

    @Autowired
    public HearingScheduler(Producer producer, ReScheduleRequestRepository repository, Configuration configuration, ObjectMapper mapper, CalendarService calendarService, HearingService hearingService, DefaultMasterDataHelper helper, ServiceConstants serviceConstants) {
        this.producer = producer;
        this.repository = repository;
        this.configuration = configuration;
        this.mapper = mapper;
        this.calendarService = calendarService;
        this.hearingService = hearingService;
        this.helper = helper;
        this.serviceConstants = serviceConstants;
    }


    public void scheduleHearingForApprovalStatus(ReScheduleHearingRequest reScheduleHearingsRequest) {
        try {
            log.info("operation = scheduleHearingForApprovalStatus, result = IN_PROGRESS, RescheduledRequest = {}", reScheduleHearingsRequest.getReScheduleHearing());
            List<ReScheduleHearing> hearingsNeedToBeSchedule = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            HashMap<String, LocalDate> dateMap = new HashMap<>();
            List<ReScheduleHearing> blockedHearings = new ArrayList<>();
            for (ReScheduleHearing element : reScheduleHearingsRequest.getReScheduleHearing()) {
                if (Objects.equals(element.getWorkflow().getAction(), "SCHEDULE")) {
                    hearingsNeedToBeSchedule.add(element);
                    ids.add(element.getHearingBookingId());
                    dateMap.put(element.getHearingBookingId(), element.getScheduleDate());
                }
                if (Objects.equals(element.getWorkflow().getAction(), "APPROVE")) {
                    blockedHearings.add(element);

                }
            }

            ReScheduleHearingRequest request = ReScheduleHearingRequest.builder().reScheduleHearing(blockedHearings)
                    .requestInfo(reScheduleHearingsRequest.getRequestInfo()).build();

            if (!blockedHearings.isEmpty()) producer.push("schedule-hearing-to-block-calendar", request);

            if (!hearingsNeedToBeSchedule.isEmpty()) {

                List<ScheduleHearing> hearings = hearingService.search(HearingSearchRequest.builder().criteria(HearingSearchCriteria.builder()
                                .hearingIds(ids).build())
                        .build());
                for (ScheduleHearing hearing : hearings) {
                    hearing.setStatus(Status.SCHEDULED);
                    hearing.setDate(dateMap.get(hearing.getHearingBookingId()));
                }

                List<MdmsSlot> defaultSlots = helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME);

                List<MdmsHearing> defaultHearings = helper.getDataFromMDMS(MdmsHearing.class, serviceConstants.DEFAULT_HEARING_MASTER_NAME);
                Map<String, MdmsHearing> hearingTypeMap = defaultHearings.stream().collect(Collectors.toMap(
                        MdmsHearing::getHearingType,
                        obj -> obj
                ));

                ScheduleHearingRequest updateRequest = ScheduleHearingRequest.builder().hearing(hearings)
                        .requestInfo(reScheduleHearingsRequest.getRequestInfo()).build();
                hearingService.updateBulk(updateRequest, defaultSlots, hearingTypeMap);
            }



        } catch (Exception e) {
            log.info("operation = scheduleHearingForApprovalStatus, result = FAILURE, message={}", e.getMessage());
        }

    }


    // blocked judged calendar by creating temp hearings
    public void updateRequestForBlockCalendar(HashMap<String, Object> record) {

        try {
            log.info("operation = updateRequestForBlockCalendar, result = IN_PROGRESS, record = {}", record);

            ReScheduleHearingRequest hearingUpdateRequest = mapper.convertValue(record, ReScheduleHearingRequest.class);
            RequestInfo requestInfo = hearingUpdateRequest.getRequestInfo();

            List<ReScheduleHearing> hearingDetails = hearingUpdateRequest.getReScheduleHearing();
            String tenantId = hearingDetails.get(0).getTenantId();

            for (ReScheduleHearing hearingDetail : hearingDetails) {

                List<AvailabilityDTO> availability = calendarService.getJudgeAvailability(JudgeAvailabilitySearchRequest
                        .builder()
                        .requestInfo(requestInfo)
                        .criteria(JudgeAvailabilitySearchCriteria.builder()
                                .judgeId(hearingDetail.getJudgeId())
                                .fromDate(hearingDetail.getAvailableAfter())
                                .courtId("0001")  //TODO: need to configure somewhere
                                .numberOfSuggestedDays(5) //TODO: later we change this to no of attendees
                                .tenantId(tenantId)
                                .build()).build());

                // update here all the suggestedDay in reschedule hearing day

                List<LocalDate> suggestedDays = availability.stream().map(
                                (suggestedDate) -> LocalDate.parse(suggestedDate.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .toList();
                hearingDetail.setSuggestedDates(suggestedDays);
                hearingDetail.setRowVersion(hearingDetail.getRowVersion() + 1);


                List<ScheduleHearing> hearings = hearingService.search(HearingSearchRequest.builder()
                        .requestInfo(requestInfo)
                        .criteria(HearingSearchCriteria.builder()
                                .hearingIds(Collections.singletonList(hearingDetail.getHearingBookingId()))
                                .build()).build());
                ScheduleHearing hearing = hearings.get(0);
                hearings.get(0).setStatus(Status.RE_SCHEDULED);


                //reschedule hearing to unblock the calendar
                hearingService.update(ScheduleHearingRequest.builder()
                        .requestInfo(requestInfo).hearing(hearings).build());


                List<ScheduleHearing> udpateHearingList = new ArrayList<>();

                for (AvailabilityDTO availabilityDTO : availability) {
                    //TODO: update logic to assign start time and end time

                    ScheduleHearing scheduleHearing = new ScheduleHearing(hearing);

                    scheduleHearing.setDate(LocalDate.parse(availabilityDTO.getDate()));
                    scheduleHearing.setStartTime(LocalDateTime.of(scheduleHearing.getDate(), hearing.getStartTime().toLocalTime()));
                    scheduleHearing.setEndTime(LocalDateTime.of(scheduleHearing.getDate(), hearing.getEndTime().toLocalTime()));
                    scheduleHearing.setStatus(Status.BLOCKED);
                    udpateHearingList.add(scheduleHearing);
                    scheduleHearing.setRescheduleRequestId(hearingDetail.getRescheduledRequestId());

                }
                hearingService.schedule(ScheduleHearingRequest.builder()
                        .requestInfo(requestInfo).hearing(udpateHearingList).build());

            }
            log.info("operation = updateRequestForBlockCalendar, result = SUCCESS");

            producer.push(configuration.getUpdateRescheduleRequestTopic(), hearingDetails);
        } catch (Exception e) {
            log.error("KAFKA_PROCESS_ERROR:", e);
            log.error("DK_SH_APP_ERR: error while blocking the calendar", e);
        }
    }

    public void checkAndScheduleHearingForOptOut(HashMap<String, Object> record) {


        try {
            log.info("operation = checkAndScheduleHearingForOptOut, result = IN_PROGRESS, record = {}", record);

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
            log.info("operation = checkAndScheduleHearingForOptOut, result = SUCCESS");

        } catch (Exception e) {
            log.error("KAFKA_PROCESS_ERROR:", e);
            log.info("operation = checkAndScheduleHearingForOptOut, result = FAILURE, message = {}", e.getMessage());

        }
    }
}

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
public class HearingScheduler {

    @Autowired
    private Producer producer;


    @Autowired
    private ReScheduleRequestRepository repository;

    @Autowired
    private Configuration configuration;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private HearingService hearingService;


    public void scheduleHearingForApprovalStatus(ReScheduleHearingRequest reScheduleHearingsRequest) {

        List<ReScheduleHearing> hearingsNeedToBeSchedule = reScheduleHearingsRequest.getReScheduleHearing()
                .stream()
                .filter((element) -> Objects.equals(element.getWorkflow().getAction(), "APPROVE"))
                .toList();

        ReScheduleHearingRequest request = ReScheduleHearingRequest.builder().reScheduleHearing(hearingsNeedToBeSchedule)
                .requestInfo(reScheduleHearingsRequest.getRequestInfo()).build();

        if (!hearingsNeedToBeSchedule.isEmpty()) producer.push("schedule-hearing-to-block-calendar", request);
    }


    // blocked judged calendar by creating temp hearings
    public void updateRequestForBlockCalendar(HashMap<String, Object> record) {

        try {

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

                }
                hearingService.schedule(ScheduleHearingRequest.builder()
                        .requestInfo(requestInfo).hearing(udpateHearingList).build());

            }
            producer.push(configuration.getUpdateRescheduleRequestTopic(), hearingDetails);
        } catch (Exception e) {
            log.error("KAFKA_PROCESS_ERROR:", e);
            log.error("DK_SH_APP_ERR: error while blocking the calendar", e);
        }
    }

    public void checkAndScheduleHearingForOptOut(HashMap<String, Object> record) {


        try {
            OptOutRequest optOutRequest = mapper.convertValue(record, OptOutRequest.class);
            RequestInfo requestInfo = optOutRequest.getRequestInfo();

            List<OptOut> optOuts = optOutRequest.getOptOuts();

            optOuts.forEach((optOut -> {


                Collections.sort(optOut.getOptoutDates());


                // get the list and cancelled the hearings
                List<ScheduleHearing> hearingList = hearingService.search(HearingSearchRequest
                        .builder().requestInfo(requestInfo)
                        .criteria(HearingSearchCriteria.builder()
                                .tenantId(optOut.getTenantId())
                                .caseId(optOut.getCaseId())
                                .judgeId(optOut.getJudgeId())
                                .fromDate(optOut.getOptoutDates().get(0))
                                .toDate(optOut.getOptoutDates().get(optOut.getOptoutDates().size() - 1))
                                .status(Collections.singletonList(Status.BLOCKED)).build()).build());

                hearingList.forEach(hearing -> hearing.setStatus(Status.CANCELLED));

                //release judge calendar
                hearingService.update(ScheduleHearingRequest.builder()
                        .requestInfo(RequestInfo.builder().build())
                        .hearing(hearingList).build());


                //TODO: get list of litigants
                //TODO: get opt out of litigants


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


                producer.push(configuration.getUpdateRescheduleRequestTopic(),reScheduleRequest);

            }));
        } catch (Exception e) {
            log.error("KAFKA_PROCESS_ERROR:", e);
        }
    }
}

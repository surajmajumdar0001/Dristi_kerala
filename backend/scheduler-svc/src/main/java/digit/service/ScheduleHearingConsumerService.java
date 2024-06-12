package digit.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.kafka.Producer;
import digit.web.models.*;
import digit.web.models.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class ScheduleHearingConsumerService {

    private final Configuration configuration;

    private final ObjectMapper mapper;

    private final CalendarService calendarService;

    private final Producer producer;

    private final HearingService hearingService;

    public ScheduleHearingConsumerService(Configuration configuration, ObjectMapper mapper, CalendarService calendarService, Producer producer, HearingService hearingService) {
        this.configuration = configuration;
        this.mapper = mapper;
        this.calendarService = calendarService;
        this.producer = producer;
        this.hearingService = hearingService;
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
                                .build()).build(), null, null);
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
}

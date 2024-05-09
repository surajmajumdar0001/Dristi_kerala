package digit.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import digit.kafka.Producer;
import digit.web.models.*;
import digit.web.models.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class HearingScheduler {

    @Autowired
    private Producer producer;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private HearingService hearingService;


    public void scheduleHearingForApprovalStatus(ReScheduleHearingRequest reScheduleHearingsRequest) {

        List<ReScheduleHearing> hearingsNeedToBeSchedule = reScheduleHearingsRequest.getReScheduleHearing().stream().filter((element) -> Objects.equals(element.getWorkflow().getAction(), "APPROVE")).toList();

        ReScheduleHearingRequest request = ReScheduleHearingRequest.builder().reScheduleHearing(hearingsNeedToBeSchedule).requestInfo(reScheduleHearingsRequest.getRequestInfo()).build();

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

                List<AvailabilityDTO> availability = calendarService.getJudgeAvailability(JudgeAvailabilitySearchRequest.builder().requestInfo(hearingUpdateRequest.getRequestInfo()).criteria(JudgeAvailabilitySearchCriteria.builder().judgeId(hearingDetail.getJudgeId()).fromDate(hearingDetail.getAvailableAfter()).courtId("0001").numberOfSuggestedDays(5) //TODO: later we change this to no of attendees
                        .tenantId(tenantId)// need to configure some where
                        .build()).build());

                List<ScheduleHearing> hearings = hearingService.search(HearingSearchRequest.builder().requestInfo(requestInfo).criteria(HearingSearchCriteria.builder().hearingIds(Collections.singletonList(hearingDetail.getHearingBookingId())).build()

                ).build());
                ScheduleHearing hearing = hearings.get(0);

                List<ScheduleHearing> udpateHearingList = new ArrayList<>();

                for (AvailabilityDTO availabilityDTO : availability) {

                    hearing.setDate(LocalDate.parse(availabilityDTO.getDate()));
                    hearing.setStartTime(LocalDateTime.of(hearing.getDate(), hearing.getStartTime().toLocalTime()));
                    hearing.setEndTime(LocalDateTime.of(hearing.getDate(), hearing.getEndTime().toLocalTime()));
                    hearing.setStatus(Status.BLOCKED);
                    udpateHearingList.add(hearing);

                }
                hearingService.update(ScheduleHearingRequest.builder().requestInfo(requestInfo).hearing(udpateHearingList).build());
            }
        } catch (Exception e) {
            log.error("KAFKA_PROCESS_ERROR:", e);
        }
    }

    public void checkAndScheduleHearingForOptOut(HashMap<String, Object> record) {


        try {
            OptOutRequest optOutRequest = mapper.convertValue(record, OptOutRequest.class);
            RequestInfo requestInfo = optOutRequest.getRequestInfo();

            List<OptOut> optOuts = optOutRequest.getOptOuts();

            optOuts.forEach((optOut -> {

                List<ScheduleHearing> hearingList = hearingService.search(HearingSearchRequest.builder().requestInfo(requestInfo)
                        .criteria(HearingSearchCriteria.builder()
                                .tenantId(optOut.getTenantId())
                                .caseId(optOut.getCaseId())
                                .judgeId(optOut.getJudgeId())
                                .status(Status.BLOCKED).build()).build());

            }));
        } catch (Exception e) {
            log.error("KAFKA_PROCESS_ERROR:", e);
        }
    }
}

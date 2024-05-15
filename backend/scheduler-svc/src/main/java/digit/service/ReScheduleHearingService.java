package digit.service;


import digit.config.Configuration;
import digit.enrichment.ReScheduleRequestEnrichment;
import digit.kafka.Producer;
import digit.repository.ReScheduleRequestRepository;
import digit.validator.ReScheduleRequestValidator;
import digit.web.models.*;
import digit.web.models.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ReScheduleHearingService {


    private final Configuration config;
    private ReScheduleRequestRepository repository;
    private ReScheduleRequestValidator validator;
    private ReScheduleRequestEnrichment enrichment;
    private Producer producer;
    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private HearingService hearingService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private HearingScheduler hearingScheduler;

    @Autowired
    public ReScheduleHearingService(ReScheduleRequestRepository repository, ReScheduleRequestValidator validator, ReScheduleRequestEnrichment enrichment, Producer producer, Configuration config) {
        this.repository = repository;
        this.validator = validator;
        this.enrichment = enrichment;
        this.producer = producer;
        this.config = config;
    }

    public List<ReScheduleHearing> create(ReScheduleHearingRequest reScheduleHearingsRequest) {
        List<ReScheduleHearing> reScheduleHearing = reScheduleHearingsRequest.getReScheduleHearing();

        validator.validateRescheduleRequest(reScheduleHearingsRequest);

        enrichment.enrichRescheduleRequest(reScheduleHearingsRequest);

        workflowService.updateWorkflowStatus(reScheduleHearingsRequest);

        producer.push(config.getRescheduleRequestCreateTopic(), reScheduleHearing);

        return reScheduleHearing;

    }

    public List<ReScheduleHearing> update(ReScheduleHearingRequest reScheduleHearingsRequest) {

        List<ReScheduleHearing> existingReScheduleHearingsReq = validator.validateExistingApplication(reScheduleHearingsRequest);

        enrichment.enrichRequestOnUpdate(reScheduleHearingsRequest, existingReScheduleHearingsReq);

        workflowService.updateWorkflowStatus(reScheduleHearingsRequest);

        // here if its approved we need to calculate date
        // then schedule dummy hearings for judge to block the calendar
        hearingScheduler.scheduleHearingForApprovalStatus(reScheduleHearingsRequest);

        producer.push(config.getUpdateRescheduleRequestTopic(), reScheduleHearingsRequest.getReScheduleHearing());

        return reScheduleHearingsRequest.getReScheduleHearing();

    }

    public List<ReScheduleHearing> search(ReScheduleHearingReqSearchRequest request) {
        return repository.getReScheduleRequest(request.getCriteria());
    }

    public List<ReScheduleHearing> bulkReschedule(BulkReScheduleHearingRequest request) {

        BulkReschedulingOfHearings bulkRescheduling = request.getBulkRescheduling();

        String tenantId = request.getRequestInfo().getUserInfo().getTenantId();
        String judgeId = bulkRescheduling.getJudgeId();
        LocalDateTime endTime = bulkRescheduling.getEndTime();
        LocalDateTime startTime = bulkRescheduling.getStartTime();
        LocalDate fromDate = bulkRescheduling.getFromDate();

        HearingSearchCriteria criteria = HearingSearchCriteria.builder().judgeId(judgeId).startDateTime(startTime).endDateTime(endTime).tenantId(tenantId)
                .status(Collections.singletonList(Status.SCHEDULED)).build();

        List<ScheduleHearing> hearings = hearingService.search(HearingSearchRequest.builder().requestInfo(request.getRequestInfo()).criteria(criteria).build());

        if (CollectionUtils.isEmpty(hearings)) {
            return new ArrayList<>();
        }

        List<ReScheduleHearing> reScheduleHearings = createReschedulingRequest(hearings, request.getRequestInfo().getUserInfo().getUuid());
        // create rescheduling req in db
        create(ReScheduleHearingRequest.builder().reScheduleHearing(reScheduleHearings).requestInfo(request.getRequestInfo()).build());

        //get available date
        List<AvailabilityDTO> availability = calendarService.getJudgeAvailability(
                JudgeAvailabilitySearchRequest.builder().requestInfo(request.getRequestInfo())
                        .criteria(JudgeAvailabilitySearchCriteria.builder()
                                .judgeId(judgeId)
                                .fromDate(fromDate)
                                .courtId("0001")
                                .numberOfSuggestedDays(hearings.size())
                                .tenantId(tenantId)// need to configure some where
                                .build()).build()
        );

        // assign slots and push for schedule hearing

        List<LocalDateTime> dateTime = new ArrayList<>();
        LocalDateTime startTimeOfHearing = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(10, 0));
        LocalDateTime endTimeOfHearing = null;
        int index = 0;
        for (ScheduleHearing hearing : hearings) {

            Double occupiedBandwidth = availability.get(index).getOccupiedBandwidth();
            if (8.0 - occupiedBandwidth > 1.0) {  // need to configure
                hearing.setDate(LocalDate.parse(availability.get(index).getDate()));
                if (!dateTime.isEmpty()) {
                    hearing.setStartTime(dateTime.get(index).plusMinutes(30));
                    hearing.setEndTime(dateTime.get(index).plusHours(1));
                    dateTime.add(dateTime.get(index).plusHours(1));
                } else {
                    hearing.setStartTime(startTimeOfHearing);
                    hearing.setEndTime(startTimeOfHearing.plusHours(1));
                    dateTime.add(startTimeOfHearing.plusHours(1));
                }

                availability.get(index).setOccupiedBandwidth(occupiedBandwidth + 1.0);  // need to configure
            } else {
                hearing.setDate(LocalDate.parse(availability.get(++index).getDate()));
                hearing.setStartTime(startTimeOfHearing);
                hearing.setEndTime(startTimeOfHearing.plusHours(1));
                dateTime.add(startTimeOfHearing.plusHours(1));
                availability.get(index).setOccupiedBandwidth(availability.get(index).getOccupiedBandwidth() + 1.0);  // need to configure
            }
        }


        // updated hearing in hearing table
        hearingService.update(ScheduleHearingRequest.builder()
                .hearing(hearings).requestInfo(request.getRequestInfo()).build());


        return reScheduleHearings;
    }

    private List<ReScheduleHearing> createReschedulingRequest(List<ScheduleHearing> hearings, String requesterId) {
        List<ReScheduleHearing> resultList = new ArrayList<>();

        Workflow workflow = Workflow.builder().action("AUTO_SCHEDULE").assignees(new ArrayList<>()).comment("bulk reschedule by :" + requesterId).build();
        for (ScheduleHearing hearing : hearings) {

            ReScheduleHearing reScheduleHearingReq = ReScheduleHearing.builder()
                    .hearingBookingId(hearing.getHearingBookingId())
                    .judgeId(hearing.getJudgeId())
                    .caseId(hearing.getCaseId())
                    .tenantId(hearing.getTenantId())
                    .requesterId(requesterId)
                    .workflow(workflow)
                    .actionComment("AUTO SCHEDULE BY JUDGE")
                    .reason("reschedule by judge")
                    .build();

            resultList.add(reScheduleHearingReq);

        }
        return resultList;
    }
}

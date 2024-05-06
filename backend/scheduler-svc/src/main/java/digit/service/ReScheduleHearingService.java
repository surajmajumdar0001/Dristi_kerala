package digit.service;


import digit.config.Configuration;
import digit.enrichment.ReScheduleRequestEnrichment;
import digit.helper.HearingScheduler;
import digit.kafka.Producer;
import digit.repository.ReScheduleRequestRepository;
import digit.validator.ReScheduleRequestValidator;
import digit.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReScheduleHearingService {


    private ReScheduleRequestRepository repository;

    private ReScheduleRequestValidator validator;

    private ReScheduleRequestEnrichment enrichment;

    private Producer producer;

    private final Configuration config;

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

//        workflowService.updateWorkflowStatus(reScheduleHearingsRequest);

        producer.push(config.getRescheduleRequestCreateTopic(), reScheduleHearing);


        return reScheduleHearing;

    }

    public List<ReScheduleHearing> update(ReScheduleHearingRequest reScheduleHearingsRequest) {

        List<ReScheduleHearing> reScheduleHearing = reScheduleHearingsRequest.getReScheduleHearing();

        List<ReScheduleHearing> existingReScheduleHearingsReq = validator.validateExistingApplication(reScheduleHearingsRequest);

        enrichment.enrichRequestOnUpdate(reScheduleHearingsRequest, existingReScheduleHearingsReq);

        workflowService.updateWorkflowStatus(reScheduleHearingsRequest);

        // here if its approved we need to calculate date
        // then schedule dummy hearings for judge to block the calendar
        hearingScheduler.scheduleHearingForApprovalStatus(reScheduleHearingsRequest);

        producer.push(config.getUpdateRescheduleRequestTopic(), reScheduleHearing);

        return reScheduleHearing;

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

        HearingSearchCriteria criteria = HearingSearchCriteria.builder().judgeId(judgeId).startDateTime(startTime).endDateTime(endTime).tenantId(tenantId).build();

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

        int availabilityIndex = 0;
        ///   setting start time and end time
        for (ScheduleHearing hearing : hearings) {

            AvailabilityDTO slot = availability.get(availabilityIndex);
            double occupiedBandwidth = slot.getOccupiedBandwidth();
            double remainingBandwidth = 8.0 - occupiedBandwidth;

            if (remainingBandwidth > 1.0) {
                hearing.setDate(LocalDate.parse(slot.getDate()));

                LocalDateTime start, end;
                if (!dateTime.isEmpty()) {
                    start = dateTime.get(availabilityIndex).plusMinutes(30);
                    end = start.plusHours(1);
                } else {
                    start = startTimeOfHearing;
                    end = start.plusHours(1);
                }
                hearing.setStartTime(start);
                hearing.setEndTime(end);
                dateTime.add(end);

                slot.setOccupiedBandwidth(occupiedBandwidth + 1.0);
            } else {
                // If bandwidth is not enough, move to the next availability slot
                availabilityIndex++;
                // Decrement index to process the same hearing in the next loop iteration
                continue;
            }

            availabilityIndex++; // Move to the next availability slot
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
                    .build();

            resultList.add(reScheduleHearingReq);

        }
        return resultList;
    }
}

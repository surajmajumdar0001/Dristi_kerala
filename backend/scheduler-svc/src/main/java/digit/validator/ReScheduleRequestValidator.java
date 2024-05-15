package digit.validator;


import digit.repository.ReScheduleRequestRepository;
import digit.service.HearingService;
import digit.web.models.*;
import digit.web.models.enums.Status;
import org.apache.commons.lang3.ObjectUtils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReScheduleRequestValidator {

    @Autowired
    private HearingService hearingService;

    @Autowired
    private ReScheduleRequestRepository repository;

    public void validateRescheduleRequest(ReScheduleHearingRequest reScheduleHearingsRequest) {

        List<ReScheduleHearing> rescheduleRequests = reScheduleHearingsRequest.getReScheduleHearing();

        rescheduleRequests.forEach((element) -> {
            if (ObjectUtils.isEmpty(element.getTenantId())) {
                throw new CustomException("DK_SH_APP_ERR", "tenantId is necessary to process the hearing reschedule");
            }
            if (ObjectUtils.isEmpty(element.getHearingBookingId())) {
                throw new CustomException("DK_SH_APP_ERR", " Hearing ID is necessary to process the hearing reschedule");
            }

            if (ObjectUtils.isEmpty(element.getRequesterId())) {
                throw new CustomException("DK_SH_APP_ERR", "Requester ID is necessary to process the hearing reschedule");
            }

            if (ObjectUtils.isEmpty(element.getReason())) {
                throw new CustomException("DK_SH_APP_ERR", "Reason is necessary to process the hearing reschedule");
            }

            if (ObjectUtils.isEmpty(element.getJudgeId())) {
                throw new CustomException("DK_SH_APP_ERR", "Judge ID is necessary to process the hearing reschedule");
            }

            if (ObjectUtils.isEmpty(element.getCaseId())) {
                throw new CustomException("DK_SH_APP_ERR", "Case ID is necessary to process the hearing reschedule");
            }

            //TODO: provide other required fields

            // order by latest request(last modified time)
            List<ReScheduleHearing> reScheduleRequest = repository.getReScheduleRequest(ReScheduleHearingReqSearchCriteria.builder().hearingBookingId(element.getHearingBookingId()).tenantId(element.getTenantId()).build());
            // we are checking only latest request
            if (element.getWorkflow().getAction().equals("APPLY") && !reScheduleRequest.isEmpty() && !reScheduleRequest.get(0).getStatus().equals(Status.HEARING_SCHEDULE)) {
                throw new CustomException("DK_SH_APP_ERR", "A reschedule request has already been initiated for Hearing :" + element.getHearingBookingId());
            }
        });
        List<String> ids = rescheduleRequests.stream().map(ReScheduleHearing::getHearingBookingId).toList();


        List<ScheduleHearing> hearingsToReschedule = hearingService.search(HearingSearchRequest.builder().requestInfo(reScheduleHearingsRequest.getRequestInfo()).criteria(HearingSearchCriteria.builder().hearingIds(ids).build()).build());

        if (hearingsToReschedule.size() != ids.size()) {
            throw new CustomException("DK_SH_APP_ERR", "Hearing does not exist in the database");
        }

    }

    public List<ReScheduleHearing> validateExistingApplication(ReScheduleHearingRequest reScheduleHearingsRequest) {
        List<ReScheduleHearing> reScheduleHearing = reScheduleHearingsRequest.getReScheduleHearing();

        List<String> ids = reScheduleHearing.stream().map(ReScheduleHearing::getRescheduledRequestId).toList();

        List<ReScheduleHearing> existingReScheduleRequests = repository.getReScheduleRequest(ReScheduleHearingReqSearchCriteria.builder().rescheduledRequestId(ids).build());
        if (existingReScheduleRequests.size() != ids.size()) {
            //TODO: proper error msg
            throw new CustomException("DK_SH_APP_ERR", "Reschedule request does not exist in the database");
        }

        return existingReScheduleRequests;

    }

    public void validateBulkRescheduleRequest(BulkReScheduleHearingRequest request) {

        BulkReschedulingOfHearings bulkRescheduling = request.getBulkRescheduling();


        LocalDateTime currentDateTime = LocalDateTime.now();

        if (ObjectUtils.isEmpty(bulkRescheduling.getJudgeId())) {
            throw new CustomException("DK_SH_APP_ERR", "judge id must not be null");
        }
        LocalDateTime startTime = bulkRescheduling.getStartTime();
        if (startTime.isBefore(currentDateTime)) {
            throw new CustomException("DK_SH_APP_ERR", "Can not reschedule for past date hearings");
        }

        LocalDateTime endTime = bulkRescheduling.getEndTime();
        if (endTime.isBefore(startTime)) {
            throw new CustomException("DK_SH_APP_ERR", "end time is before start time");
        }

        LocalDate scheduleAfter = bulkRescheduling.getScheduleAfter();
        if (scheduleAfter.isBefore(LocalDate.now())) {
            throw new CustomException("DK_SH_APP_ERR", "can not reschedule for past dates");
        }

    }
}

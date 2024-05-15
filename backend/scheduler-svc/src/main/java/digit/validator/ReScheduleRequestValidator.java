package digit.validator;


import digit.repository.ReScheduleRequestRepository;
import digit.service.HearingService;
import digit.web.models.*;
import digit.web.models.enums.Status;
import org.apache.commons.lang3.ObjectUtils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


            List<ReScheduleHearing> reScheduleRequest = repository.getReScheduleRequest(ReScheduleHearingReqSearchCriteria.builder()
                    .hearingBookingId(element.getHearingBookingId()).tenantId(element.getTenantId())
                    .status(Status.APPLIED).build());

            if (element.getWorkflow().getAction().equals("APPLY") && !reScheduleRequest.isEmpty()) {
                throw new CustomException("DK_SH_APP_ERR", "A reschedule request has already been initiated for Hearing :" + element.getHearingBookingId());
            }
        });
        List<String> ids = rescheduleRequests.stream().map(ReScheduleHearing::getHearingBookingId).toList();

        List<ScheduleHearing> hearingsToReschedule = hearingService.search(HearingSearchRequest.builder().requestInfo(reScheduleHearingsRequest.getRequestInfo())
                .criteria(HearingSearchCriteria.builder().hearingIds(ids).build()).build());

        if (hearingsToReschedule.size() != ids.size()) {
            throw new CustomException("DK_SH_APP_ERR", "Hearing does not exist in the database");
        }

    }

    public List<ReScheduleHearing> validateExistingApplication(ReScheduleHearingRequest reScheduleHearingsRequest) {
        List<ReScheduleHearing> reScheduleHearing = reScheduleHearingsRequest.getReScheduleHearing();

        List<String> ids = reScheduleHearing.stream().map(ReScheduleHearing::getRescheduledRequestId).toList();
        for(ReScheduleHearing reHearing : reScheduleHearing){
            if(reHearing.getStatus().equals(Status.APPLIED) && reHearing.getAvailableAfter() == null){
                throw new CustomException("DK_SH_APP_ERR", "Available after day is required.");
            }
        }
        List<ReScheduleHearing> existingReScheduleRequests = repository.getReScheduleRequest(ReScheduleHearingReqSearchCriteria.builder().rescheduledRequestId(ids).build());
        if (existingReScheduleRequests.size() != ids.size()) {
            //TODO: proper error msg
            throw new CustomException("DK_SH_APP_ERR", "Reschedule request does not exist in the database");
        }


        return existingReScheduleRequests;

    }
}

package digit.validator;


import digit.repository.ReScheduleRequestRepository;
import digit.service.HearingService;
import digit.web.models.*;
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

        rescheduleRequests.stream().peek((element) -> {
            if (ObjectUtils.isEmpty(element.getTenantId())) {
                throw new CustomException("DK_SH_APP_ERR", "tenantId is required to reschedule the hearing");
            }
            if (ObjectUtils.isEmpty(element.getHearingBookingId())) {
                throw new CustomException("DK_SH_APP_ERR", "hearing Id is required to reschedule the hearing");
            }

            //TODO: provide other required fields
        });
        List<String> ids = rescheduleRequests.stream().map(ReScheduleHearing::getHearingBookingId).toList();

        List<ScheduleHearing> hearingsToReschedule = hearingService.search(HearingSearchRequest.builder().requestInfo(reScheduleHearingsRequest.getRequestInfo())
                .criteria(HearingSearchCriteria.builder().hearingIds(ids).build()).build());

        if (hearingsToReschedule.size() != ids.size()) {
            //TODO: proper error msg
            throw new CustomException("DK_SH_APP_ERR", "Hearing does not exist in db");
        }
    }

    public  List<ReScheduleHearing> validateExistingApplication(ReScheduleHearingRequest reScheduleHearingsRequest) {
        List<ReScheduleHearing> reScheduleHearing = reScheduleHearingsRequest.getReScheduleHearing();

        List<String> ids = reScheduleHearing.stream().map(ReScheduleHearing::getRescheduledRequestId).toList();

        List<ReScheduleHearing> existingReScheduleRequests = repository.getReScheduleRequest(ReScheduleHearingReqSearchCriteria.builder().rescheduledRequestId(ids).build());
        if (existingReScheduleRequests.size() != ids.size()) {
            //TODO: proper error msg
            throw new CustomException("DK_SH_APP_ERR", "Request does not exist in db");
        }

        return existingReScheduleRequests;

    }
}

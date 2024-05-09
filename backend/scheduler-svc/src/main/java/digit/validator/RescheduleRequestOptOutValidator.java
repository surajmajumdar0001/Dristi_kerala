package digit.validator;


import digit.repository.RescheduleRequestOptOutRepository;
import digit.service.ReScheduleHearingService;
import digit.web.models.*;
import org.apache.commons.lang3.ObjectUtils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RescheduleRequestOptOutValidator {

    @Autowired
    private RescheduleRequestOptOutRepository repository;

    @Autowired
    private ReScheduleHearingService reScheduleHearingService;

    public void validateRequest(OptOutRequest request) {

        request.getOptOuts().forEach(application -> {
            if (ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException("DK_SH_APP_ERR", "tenantId is mandatory for opt out dates");
            if (ObjectUtils.isEmpty(application.getIndividualId()))
                throw new CustomException("DK_SH_APP_ERR", "individual id is mandatory for opt out dates");
            if (ObjectUtils.isEmpty(application.getRescheduleRequestId()))
                throw new CustomException("DK_SH_APP_ERR", "reschedule request id is mandatory for opt out dates");


            //TODO:validate the person opting out have relation to case
            //for this call case api

        });

        // validate reschedule request exist in db
        List<String> ids = request.getOptOuts().stream().map((OptOut::getRescheduleRequestId)).toList();

        List<ReScheduleHearing> search = reScheduleHearingService.search(ReScheduleHearingReqSearchRequest.builder()
                .requestInfo(request.getRequestInfo())
                .criteria(ReScheduleHearingReqSearchCriteria.builder().rescheduledRequestId(ids).build()).build());

        if (ids.size() != search.size()) {
            throw new CustomException("DK_SH_APP_ERR", "Reschedule request does not exist in database");
        }
    }

    public void validateUpdateRequest(OptOutRequest request) {


    }
}

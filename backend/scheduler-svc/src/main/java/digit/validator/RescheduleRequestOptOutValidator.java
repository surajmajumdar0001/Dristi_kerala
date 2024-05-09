package digit.validator;


import digit.repository.RescheduleRequestOptOutRepository;
import digit.web.models.OptOut;
import digit.web.models.OptOutRequest;
import digit.web.models.OptOutSearchCriteria;
import org.apache.commons.lang3.ObjectUtils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RescheduleRequestOptOutValidator {

    @Autowired
    private RescheduleRequestOptOutRepository repository;

    public void validateRequest(OptOutRequest request) {

        request.getOptOuts().forEach(application -> {
            if (ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException("DK_SH_APP_ERR", "tenantId is mandatory for opt out dates");
            if (ObjectUtils.isEmpty(application.getIndividualId()))
                throw new CustomException("DK_SH_APP_ERR", "individual id is mandatory for opt out dates");
            if (ObjectUtils.isEmpty(application.getRescheduleRequestId()))
                throw new CustomException("DK_SH_APP_ERR", "reschedule request id is mandatory for opt out dates");
        });
    }

    public void validateUpdateRequest(OptOutRequest request) {





    }
}

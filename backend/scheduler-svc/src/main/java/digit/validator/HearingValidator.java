package digit.validator;


import digit.web.models.ScheduleHearing;
import org.apache.commons.lang3.ObjectUtils;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HearingValidator {
    public void validateHearing(List<ScheduleHearing> schedulingRequests) {

        schedulingRequests.forEach(application -> {
            if (ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException("DK_SH_APP_ERR", "tenantId is mandatory for schedule a hearing");
        });
    }
}


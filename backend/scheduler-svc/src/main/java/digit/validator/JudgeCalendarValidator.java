package digit.validator;


import digit.web.models.JudgeCalendarRule;
import org.apache.commons.lang3.ObjectUtils;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JudgeCalendarValidator {
    public void validateUpdateJudgeCalendar(List<JudgeCalendarRule> judgeCalendarRule) {

        judgeCalendarRule.forEach(application -> {
            if (ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException("DK_SH_APP_ERR", "tenantId is mandatory for updating judge calendar");
        });
    }
}

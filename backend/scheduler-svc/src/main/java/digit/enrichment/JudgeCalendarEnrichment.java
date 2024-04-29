package digit.enrichment;

import digit.web.models.JudgeCalendarRule;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class JudgeCalendarEnrichment {


    public void enrichUpdateJudgeCalendar(RequestInfo requestInfo, List<JudgeCalendarRule> judgeCalendarRule) {


        judgeCalendarRule.stream().forEach((calendar) -> {

            Long currentTime = System.currentTimeMillis();
            calendar.getAuditDetails().setLastModifiedTime(currentTime);
            calendar.getAuditDetails().setLastModifiedBy(requestInfo.getUserInfo().getUuid());
            calendar.setRowVersion(calendar.getRowVersion() + 1);

        });
    }


}

package digit.enrichment;


import digit.util.IdgenUtil;
import digit.web.models.ScheduleHearing;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.AuditDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class JudgeCalendarEnrichment {

    @Autowired
    private IdgenUtil idgenUtil;



}

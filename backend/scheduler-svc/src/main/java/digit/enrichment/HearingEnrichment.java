package digit.enrichment;


import digit.config.Configuration;
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
public class HearingEnrichment {

    @Autowired
    private IdgenUtil idgenUtil;

    @Autowired
    private Configuration configuration;


    public void enrichForScheduleHearing(RequestInfo requestInfo, List<ScheduleHearing> hearingList) {

        log.info("starting update method for schedule hearing enrichment");
        log.info("generating IDs for schedule hearing enrichment using IdGenService");
        List<String> idList = idgenUtil.getIdList(requestInfo,
                hearingList.get(0).getTenantId(),
                configuration.getHearingIdFormat(), null, hearingList.size());
        int index = 0;
        for (ScheduleHearing hearing : hearingList) {
            AuditDetails auditDetails = getAuditDetailsScheduleHearing(requestInfo);
            hearing.setAuditDetails(auditDetails);
            hearing.setHearingBookingId(idList.get(index++));
        }


    }

    private AuditDetails getAuditDetailsScheduleHearing(RequestInfo requestInfo) {

        AuditDetails auditDetails = AuditDetails.builder()
                .build();
        return auditDetails;
    }
}

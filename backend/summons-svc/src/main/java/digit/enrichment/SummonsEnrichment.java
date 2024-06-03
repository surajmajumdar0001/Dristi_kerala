package digit.enrichment;

import digit.config.Configuration;
import digit.util.IdgenUtil;
import digit.web.models.Summons;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SummonsEnrichment {

    private final IdgenUtil idgenUtil;

    private final Configuration config;

    public SummonsEnrichment(IdgenUtil idgenUtil, Configuration config) {
        this.idgenUtil = idgenUtil;
        this.config = config;
    }

    public void enrichSummons(Summons summons, RequestInfo requestInfo) {
        List<String> idList = idgenUtil.getIdList(requestInfo,
                config.getEgovStateTenantId(), config.getSummonsIdFormat(), null, 1);
        AuditDetails auditDetails = getAuditDetails(requestInfo);
        summons.setAuditDetails(auditDetails);
        summons.setSummonsId(idList.get(0));
        summons.setRowVersion(1);
    }

    private AuditDetails getAuditDetails(RequestInfo requestInfo) {

        return AuditDetails.builder()
                .createdBy(requestInfo.getUserInfo().getUuid())
                .createdTime(System.currentTimeMillis())
                .lastModifiedBy(requestInfo.getUserInfo().getUuid())
                .lastModifiedTime(System.currentTimeMillis())
                .build();

    }
}

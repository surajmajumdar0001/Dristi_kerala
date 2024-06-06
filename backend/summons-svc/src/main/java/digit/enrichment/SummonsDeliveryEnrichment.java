package digit.enrichment;

import digit.config.Configuration;
import digit.web.models.SummonsDelivery;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SummonsDeliveryEnrichment {

    private final Configuration config;

    public SummonsDeliveryEnrichment(Configuration config) {
        this.config = config;
    }

    public void enrichSummonsDelivery(SummonsDelivery summonsDelivery, RequestInfo requestInfo) {
        AuditDetails auditDetails = getAuditDetails(requestInfo);
        summonsDelivery.setAuditDetails(auditDetails);
        summonsDelivery.setDeliveryStatus("DELIVERY_NOT_STARTED");
        summonsDelivery.setRowVersion(1);
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

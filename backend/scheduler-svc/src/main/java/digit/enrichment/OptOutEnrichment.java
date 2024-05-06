package digit.enrichment;


import digit.models.coremodels.AuditDetails;
import digit.web.models.OptOutRequest;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class OptOutEnrichment {
    public void enrichCreateRequest(OptOutRequest request) {

        AuditDetails auditDetails = getAuditDetailsScheduleHearing(request.getRequestInfo());

        request.getOptOuts().forEach((application)->{
            application.setId(UUID.randomUUID().toString());
            application.setAuditDetails(auditDetails);
            application.setRowVersion(1);

        });
    }

    public void enrichUpdateRequest(OptOutRequest request) {
    }

    private AuditDetails getAuditDetailsScheduleHearing(RequestInfo requestInfo) {

        return AuditDetails.builder()
                .createdBy(requestInfo.getUserInfo().getUuid())
                .createdTime(System.currentTimeMillis())
                .lastModifiedBy(requestInfo.getUserInfo().getUuid())
                .lastModifiedTime(System.currentTimeMillis())
                .build();

    }
}

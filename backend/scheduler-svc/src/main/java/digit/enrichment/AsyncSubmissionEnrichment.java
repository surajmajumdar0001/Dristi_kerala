package digit.enrichment;

import digit.config.Configuration;
import digit.util.IdgenUtil;
import digit.web.models.AsyncSubmission;
import digit.web.models.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AsyncSubmissionEnrichment {

    private final IdgenUtil idGenUtil;

    private final Configuration config;

    @Autowired
    public AsyncSubmissionEnrichment(IdgenUtil idgenUtil, Configuration config) {
        this.idGenUtil = idgenUtil;
        this.config = config;
    }

    public void enrichAsyncSubmissions(RequestInfo requestInfo, AsyncSubmission asyncSubmission) {

        log.info("generating IDs for async submission enrichment using IdGenService");
        List<String> idList = idGenUtil.getIdList(requestInfo,
                config.getEgovStateTenantId(),
                config.getAsyncSubmissionIdFormat(), null, 1);
        AuditDetails auditDetails = getAuditDetails(requestInfo);
        asyncSubmission.setAuditDetails(auditDetails);
        asyncSubmission.setSubmissionId(idList.get(0));
        asyncSubmission.setRowVersion(1);
        asyncSubmission.setStatus(Status.SCHEDULED.name());
    }


    public void enrichUpdateAsyncSubmission(RequestInfo requestInfo, AsyncSubmission asyncSubmission) {
        Long currentTime = System.currentTimeMillis();
        asyncSubmission.getAuditDetails().setLastModifiedTime(currentTime);
        asyncSubmission.getAuditDetails().setLastModifiedBy(requestInfo.getUserInfo().getUuid());
        asyncSubmission.setRowVersion(asyncSubmission.getRowVersion() + 1);
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

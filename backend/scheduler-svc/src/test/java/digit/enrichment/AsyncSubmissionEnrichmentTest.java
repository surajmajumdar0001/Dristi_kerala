package digit.enrichment;

import digit.config.Configuration;
import digit.util.IdgenUtil;
import digit.web.models.AsyncSubmission;
import digit.web.models.enums.Status;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AsyncSubmissionEnrichmentTest {

    @InjectMocks
    private AsyncSubmissionEnrichment asyncSubmissionEnrichment;

    @Mock
    private IdgenUtil idGenUtil;

    @Mock
    private Configuration config;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testEnrichAsyncSubmissions() {
        // Setup mocks
        RequestInfo requestInfo = getRequestInfo();
        AsyncSubmission asyncSubmission = new AsyncSubmission();
        List<String> idList = Collections.singletonList("test-id");

        when(config.getEgovStateTenantId()).thenReturn("test-tenant");
        when(config.getAsyncSubmissionIdFormat()).thenReturn("test-format");
        when(idGenUtil.getIdList(any(RequestInfo.class), anyString(), anyString(), any(), anyInt())).thenReturn(idList);

        // Call the method
        asyncSubmissionEnrichment.enrichAsyncSubmissions(requestInfo, asyncSubmission);

        // Assertions
        assertNotNull(asyncSubmission.getAuditDetails());
        assertEquals("test-id", asyncSubmission.getSubmissionId());
        assertEquals(1, asyncSubmission.getRowVersion());
        assertEquals(Status.SCHEDULED.name(), asyncSubmission.getStatus());
    }

    @Test
    public void testEnrichUpdateAsyncSubmission() {
        // Setup mocks
        RequestInfo requestInfo = getRequestInfo();
        AsyncSubmission asyncSubmission = new AsyncSubmission();
        AuditDetails auditDetails = getAuditDetails(requestInfo);
        asyncSubmission.setAuditDetails(auditDetails);
        asyncSubmission.setRowVersion(1);

        // Call the method
        asyncSubmissionEnrichment.enrichUpdateAsyncSubmission(requestInfo, asyncSubmission);

        // Assertions
        assertEquals(2, asyncSubmission.getRowVersion());
        assertEquals(requestInfo.getUserInfo().getUuid(), asyncSubmission.getAuditDetails().getLastModifiedBy());
    }

    @Test
    public void testGetAuditDetails() {
        // Setup mocks
        RequestInfo requestInfo = getRequestInfo();

        // Using reflection to test private method
        AuditDetails auditDetails = ReflectionTestUtils.invokeMethod(asyncSubmissionEnrichment, "getAuditDetails", requestInfo);

        // Assertions
        assertNotNull(auditDetails);
        assertEquals(requestInfo.getUserInfo().getUuid(), auditDetails.getCreatedBy());
        assertEquals(requestInfo.getUserInfo().getUuid(), auditDetails.getLastModifiedBy());
        assertNotNull(auditDetails.getCreatedTime());
        assertNotNull(auditDetails.getLastModifiedTime());
    }

    private RequestInfo getRequestInfo() {
        User user = User.builder().uuid("test-uuid").build();
        return RequestInfo.builder().userInfo(user).build();
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

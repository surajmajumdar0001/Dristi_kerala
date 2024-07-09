package digit.enrichment;

import digit.models.coremodels.AuditDetails;
import digit.web.models.OptOut;
import digit.web.models.OptOutRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RescheduleRequestOptOutEnrichmentTest {

    @InjectMocks
    private RescheduleRequestOptOutEnrichment rescheduleRequestOptOutEnrichment;

    @Mock
    private RequestInfo requestInfo;

    @Mock
    private User user;

    private OptOutRequest optOutRequest;
    private List<OptOut> optOutApplications;

    @BeforeEach
    public void setUp() {
        Mockito.lenient().when(user.getUuid()).thenReturn("test-uuid");
        Mockito.lenient().when(requestInfo.getUserInfo()).thenReturn(user);

        optOutApplications = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            OptOut application = new OptOut();
            optOutApplications.add(application);
        }

        optOutRequest = new OptOutRequest();
        optOutRequest.setRequestInfo(requestInfo);
        optOutRequest.setOptOuts(optOutApplications);
    }

    @Test
    public void testEnrichCreateRequest() {
        rescheduleRequestOptOutEnrichment.enrichCreateRequest(optOutRequest);

        assertNotNull(optOutApplications);
        assertEquals(3, optOutApplications.size());

        for (OptOut application : optOutApplications) {
            assertNotNull(application.getAuditDetails());
            assertEquals("test-uuid", application.getAuditDetails().getCreatedBy());
            assertEquals("test-uuid", application.getAuditDetails().getLastModifiedBy());
            assertEquals(1, application.getRowVersion());
        }
    }

    @Test
    public void testEnrichUpdateRequest() {
        for (OptOut application : optOutApplications) {
            AuditDetails auditDetails = new AuditDetails("old-uuid", "admin", 1L, 1L);
            application.setAuditDetails(auditDetails);
            application.setRowVersion(1);
        }

        rescheduleRequestOptOutEnrichment.enrichUpdateRequest(optOutRequest);

        for (OptOut application : optOutApplications) {
            assertNotNull(application.getAuditDetails());
            assertEquals("test-uuid", application.getAuditDetails().getLastModifiedBy());
            assertTrue(application.getAuditDetails().getLastModifiedTime() <= System.currentTimeMillis());
            assertEquals(2, application.getRowVersion());
        }
    }

    @Test
    public void testEnrichCreateRequest_WithNullRequestInfo() {
        optOutRequest.setRequestInfo(null);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            rescheduleRequestOptOutEnrichment.enrichCreateRequest(optOutRequest);
        });

        assertNotNull(exception);
    }

    @Test
    public void testEnrichCreateRequest_WithEmptyOptOuts() {
        optOutRequest.setOptOuts(new ArrayList<>());

        rescheduleRequestOptOutEnrichment.enrichCreateRequest(optOutRequest);

        assertTrue(optOutRequest.getOptOuts().isEmpty());
    }

    @Test
    public void testEnrichUpdateRequest_WithNullRequestInfo() {
        optOutRequest.setRequestInfo(null);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            rescheduleRequestOptOutEnrichment.enrichUpdateRequest(optOutRequest);
        });

        assertNotNull(exception);
    }

    @Test
    public void testEnrichUpdateRequest_WithEmptyOptOuts() {
        optOutRequest.setOptOuts(new ArrayList<>());

        rescheduleRequestOptOutEnrichment.enrichUpdateRequest(optOutRequest);

        assertTrue(optOutRequest.getOptOuts().isEmpty());
    }

//    @Test
//    public void testGetAuditDetailsScheduleHearing() {
//        AuditDetails auditDetails = rescheduleRequestOptOutEnrichment.getAuditDetailsScheduleHearing(requestInfo);
//
//        assertNotNull(auditDetails);
//        assertEquals("test-uuid", auditDetails.getCreatedBy());
//        assertEquals("test-uuid", auditDetails.getLastModifiedBy());
//        assertTrue(auditDetails.getCreatedTime() <= System.currentTimeMillis());
//        assertTrue(auditDetails.getLastModifiedTime() <= System.currentTimeMillis());
//    }
}

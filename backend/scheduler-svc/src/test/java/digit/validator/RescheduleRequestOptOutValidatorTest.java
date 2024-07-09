package digit.validator;

import com.fasterxml.jackson.databind.JsonNode;
import digit.config.Configuration;
import digit.repository.RescheduleRequestOptOutRepository;
import digit.service.ReScheduleHearingService;
import digit.util.CaseUtil;
import digit.web.models.*;
import digit.web.models.cases.CaseCriteria;
import digit.web.models.cases.SearchCaseRequest;
import digit.web.models.enums.Status;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RescheduleRequestOptOutValidatorTest {

    @InjectMocks
    private RescheduleRequestOptOutValidator validator;

    @Mock
    private RescheduleRequestOptOutRepository repository;

    @Mock
    private ReScheduleHearingService reScheduleHearingService;

    @Mock
    private Configuration config;

    @Mock
    private CaseUtil caseUtil;

    private OptOutRequest request;

    @BeforeEach
    void setUp() {
        // Initialize the request object
        request = new OptOutRequest();
        request.setRequestInfo(new RequestInfo());
        OptOut optOut = new OptOut();
        optOut.setTenantId("tenantId");
        optOut.setIndividualId("individualId");
        optOut.setRescheduleRequestId("rescheduleRequestId");
        optOut.setOptoutDates(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        optOut.setCaseId("caseId");
        request.setOptOuts(Collections.singletonList(optOut));
    }

    @Test
    void testValidateRequestWithEmptyTenantId() {
        request.getOptOuts().get(0).setTenantId(null);
        assertThrows(CustomException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testValidateRequestWithEmptyIndividualId() {
        request.getOptOuts().get(0).setIndividualId(null);
        assertThrows(CustomException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testValidateRequestWithEmptyRescheduleRequestId() {
        request.getOptOuts().get(0).setRescheduleRequestId(null);
        assertThrows(CustomException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testValidateRequestWithOptOutLimitExceeded() {
        when(config.getOptOutLimit()).thenReturn(1L);
        assertThrows(CustomException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testValidateRequestWithExistingOptOut() {
        when(config.getOptOutLimit()).thenReturn(2L);
        when(repository.getOptOut(any(), any(), any())).thenReturn(Collections.singletonList(new OptOut()));
        assertThrows(CustomException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testValidateRequestWithInvalidIndividualId() {
        JsonNode mockNode = mock(JsonNode.class);
        assertThrows(CustomException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testValidateRequestWithNonExistingRescheduleRequest() {
        assertThrows(CustomException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testValidateRequestWithNonApprovedRescheduleRequest() {
        ReScheduleHearing hearing = new ReScheduleHearing();
        hearing.setStatus(Status.REVIEW);
        hearing.setRescheduledRequestId("rescheduleRequestId");
        when(config.getOptOutLimit()).thenReturn(2L);
        assertThrows(CustomException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testValidateRequestWithInvalidOptOutDates() {
        ReScheduleHearing hearing = new ReScheduleHearing();
        hearing.setStatus(Status.APPROVED);
        hearing.setRescheduledRequestId("rescheduleRequestId");
        hearing.setSuggestedDates(Arrays.asList(LocalDate.now().plusDays(2), LocalDate.now().plusDays(3)));
        when(config.getOptOutLimit()).thenReturn(2L);
        assertThrows(CustomException.class, () -> validator.validateRequest(request));
    }

    @Test
    void testValidateRequestWithValidData() {
        ReScheduleHearing hearing = new ReScheduleHearing();
        hearing.setStatus(Status.APPROVED);
        hearing.setRescheduledRequestId("rescheduleRequestId");
        hearing.setSuggestedDates(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        when(config.getOptOutLimit()).thenReturn(2L);
        when(reScheduleHearingService.search(any(), any(), any())).thenReturn(Collections.singletonList(hearing));
        when(caseUtil.getRepresentatives(any(SearchCaseRequest.class))).thenReturn(mock(JsonNode.class));
        when(caseUtil.getIdsFromJsonNodeArray(any(JsonNode.class))).thenReturn(Collections.singleton("individualId"));
        validator.validateRequest(request);
    }
}

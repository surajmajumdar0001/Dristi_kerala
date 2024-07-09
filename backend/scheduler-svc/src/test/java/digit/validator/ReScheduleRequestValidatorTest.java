package digit.validator;

import com.fasterxml.jackson.databind.JsonNode;
import digit.config.Configuration;
import digit.repository.ReScheduleRequestRepository;
import digit.service.HearingService;
import digit.util.CaseUtil;
import digit.web.models.*;
import digit.web.models.cases.CaseCriteria;
import digit.web.models.cases.SearchCaseRequest;
import digit.web.models.enums.Status;
import org.apache.commons.lang3.ObjectUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReScheduleRequestValidatorTest {

    @InjectMocks
    private ReScheduleRequestValidator validator;

    @Mock
    private HearingService hearingService;

    @Mock
    private ReScheduleRequestRepository repository;

    @Mock
    private Configuration config;

    @Mock
    private CaseUtil caseUtil;

    private ReScheduleHearingRequest reScheduleHearingsRequest;

    @BeforeEach
    void setUp() {
        // Initialize the request object
        reScheduleHearingsRequest = new ReScheduleHearingRequest();
        reScheduleHearingsRequest.setRequestInfo(new RequestInfo());
        ReScheduleHearing reScheduleHearing = new ReScheduleHearing();
        reScheduleHearing.setTenantId("tenantId");
        reScheduleHearing.setHearingBookingId("hearingBookingId");
        reScheduleHearing.setRequesterId("requesterId");
        reScheduleHearing.setReason("reason");
        reScheduleHearing.setJudgeId("judgeId");
        reScheduleHearing.setCaseId("caseId");
        reScheduleHearing.setWorkflow(Workflow.builder().action("APPLY").build());
        reScheduleHearingsRequest.setReScheduleHearing(Collections.singletonList(reScheduleHearing));
    }

    @Test
    void testValidateRescheduleRequestWithEmptyTenantId() {
        reScheduleHearingsRequest.getReScheduleHearing().get(0).setTenantId(null);
        assertThrows(CustomException.class, () -> validator.validateRescheduleRequest(reScheduleHearingsRequest));
    }

    @Test
    void testValidateRescheduleRequestWithEmptyHearingBookingId() {
        reScheduleHearingsRequest.getReScheduleHearing().get(0).setHearingBookingId(null);
        assertThrows(CustomException.class, () -> validator.validateRescheduleRequest(reScheduleHearingsRequest));
    }

    @Test
    void testValidateRescheduleRequestWithEmptyRequesterId() {
        reScheduleHearingsRequest.getReScheduleHearing().get(0).setRequesterId(null);
        assertThrows(CustomException.class, () -> validator.validateRescheduleRequest(reScheduleHearingsRequest));
    }

    @Test
    void testValidateRescheduleRequestWithEmptyReason() {
        reScheduleHearingsRequest.getReScheduleHearing().get(0).setReason(null);
        assertThrows(CustomException.class, () -> validator.validateRescheduleRequest(reScheduleHearingsRequest));
    }

    @Test
    void testValidateRescheduleRequestWithEmptyJudgeId() {
        reScheduleHearingsRequest.getReScheduleHearing().get(0).setJudgeId(null);
        assertThrows(CustomException.class, () -> validator.validateRescheduleRequest(reScheduleHearingsRequest));
    }

    @Test
    void testValidateRescheduleRequestWithEmptyCaseId() {
        reScheduleHearingsRequest.getReScheduleHearing().get(0).setCaseId(null);
        assertThrows(CustomException.class, () -> validator.validateRescheduleRequest(reScheduleHearingsRequest));
    }

    @Test
    void testValidateRescheduleRequestWithNonExistingHearing() {
        when(hearingService.search(any(), any(), any())).thenReturn(Collections.emptyList());
        assertThrows(CustomException.class, () -> validator.validateRescheduleRequest(reScheduleHearingsRequest));
    }

    @Test
    void testValidateRescheduleRequestWithDueDatePassed() {
        ScheduleHearing hearing = new ScheduleHearing();
        hearing.setDate(LocalDate.now().minusDays(1));
        when(config.getRescheduleRequestDueDate()).thenReturn(2L);
        when(hearingService.search(any(), any(), any())).thenReturn(Collections.singletonList(hearing));
        assertThrows(CustomException.class, () -> validator.validateRescheduleRequest(reScheduleHearingsRequest));
    }

    @Test
    void testValidateRescheduleRequestWithExistingOpenRequest() {
        ReScheduleHearing existingRequest = new ReScheduleHearing();
        existingRequest.setStatus(Status.APPLIED);
        assertThrows(CustomException.class, () -> validator.validateRescheduleRequest(reScheduleHearingsRequest));
    }

    @Test
    void testValidateRescheduleRequestWithInvalidRequesterId() {
        JsonNode mockNode = mock(JsonNode.class);
        assertThrows(CustomException.class, () -> validator.validateRescheduleRequest(reScheduleHearingsRequest));
    }

    @Test
    void testValidateRescheduleRequestWithValidData() {
        ScheduleHearing hearing = new ScheduleHearing();
        hearing.setDate(LocalDate.now().plusDays(2));
        when(config.getRescheduleRequestDueDate()).thenReturn(1L);
        when(hearingService.search(any(), any(), any())).thenReturn(Collections.singletonList(hearing));
        when(caseUtil.getRepresentatives(any(SearchCaseRequest.class))).thenReturn(mock(JsonNode.class));
        when(caseUtil.getIdsFromJsonNodeArray(any(JsonNode.class))).thenReturn(Collections.singleton("requesterId"));
        validator.validateRescheduleRequest(reScheduleHearingsRequest);
    }

    @Test
    void testValidateExistingApplicationWithApproveActionAndEmptyAvailableAfter() {
        reScheduleHearingsRequest.getReScheduleHearing().get(0).setWorkflow(new Workflow());
        assertThrows(NullPointerException.class, () -> validator.validateExistingApplication(reScheduleHearingsRequest));
    }

    @Test
    void testValidateExistingApplicationWithApproveActionAndPastAvailableAfter() {
        reScheduleHearingsRequest.getReScheduleHearing().get(0).setWorkflow(new Workflow());
        reScheduleHearingsRequest.getReScheduleHearing().get(0).setAvailableAfter(LocalDate.now().minusDays(1));
        assertThrows(NullPointerException.class, () -> validator.validateExistingApplication(reScheduleHearingsRequest));
    }

    @Test
    void testValidateExistingApplicationWithNonExistingRescheduleRequest() {
        when(repository.getReScheduleRequest(any(), any(), any())).thenReturn(Collections.emptyList());
        assertThrows(CustomException.class, () -> validator.validateExistingApplication(reScheduleHearingsRequest));
    }

    @Test
    void testValidateExistingApplicationWithValidData() {
        reScheduleHearingsRequest.getReScheduleHearing().get(0).setWorkflow(Workflow.builder().action("APPROVE").build());
        reScheduleHearingsRequest.getReScheduleHearing().get(0).setAvailableAfter(LocalDate.now().plusDays(1));
        ReScheduleHearing existingRequest = new ReScheduleHearing();
        when(repository.getReScheduleRequest(any(), any(), any())).thenReturn(Collections.singletonList(existingRequest));
        List<ReScheduleHearing> result = validator.validateExistingApplication(reScheduleHearingsRequest);
        assert(result.size() == 1);
    }

    @Test
    void testValidateBulkRescheduleRequestWithEmptyJudgeId() {
        BulkReScheduleHearingRequest request = new BulkReScheduleHearingRequest();
        BulkReschedulingOfHearings bulkRescheduling = new BulkReschedulingOfHearings();
        request.setBulkRescheduling(bulkRescheduling);
        assertThrows(CustomException.class, () -> validator.validateBulkRescheduleRequest(request));
    }

    @Test
    void testValidateBulkRescheduleRequestWithPastStartTime() {
        BulkReScheduleHearingRequest request = new BulkReScheduleHearingRequest();
        BulkReschedulingOfHearings bulkRescheduling = new BulkReschedulingOfHearings();
        bulkRescheduling.setJudgeId("judgeId");
        bulkRescheduling.setStartTime(LocalDateTime.now().minusDays(1));
        request.setBulkRescheduling(bulkRescheduling);
        assertThrows(CustomException.class, () -> validator.validateBulkRescheduleRequest(request));
    }

    @Test
    void testValidateBulkRescheduleRequestWithEndTimeBeforeStartTime() {
        BulkReScheduleHearingRequest request = new BulkReScheduleHearingRequest();
        BulkReschedulingOfHearings bulkRescheduling = new BulkReschedulingOfHearings();
        bulkRescheduling.setJudgeId("judgeId");
        bulkRescheduling.setStartTime(LocalDateTime.now().plusDays(1));
        bulkRescheduling.setEndTime(LocalDateTime.now().plusDays(1).minusHours(1));
        request.setBulkRescheduling(bulkRescheduling);
        assertThrows(CustomException.class, () -> validator.validateBulkRescheduleRequest(request));
    }

    @Test
    void testValidateBulkRescheduleRequestWithPastScheduleAfter() {
        BulkReScheduleHearingRequest request = new BulkReScheduleHearingRequest();
        BulkReschedulingOfHearings bulkRescheduling = new BulkReschedulingOfHearings();
        bulkRescheduling.setJudgeId("judgeId");
        bulkRescheduling.setStartTime(LocalDateTime.now().plusDays(1));
        bulkRescheduling.setEndTime(LocalDateTime.now().plusDays(2));
        bulkRescheduling.setScheduleAfter(LocalDate.now().minusDays(1));
        request.setBulkRescheduling(bulkRescheduling);
        assertThrows(CustomException.class, () -> validator.validateBulkRescheduleRequest(request));
    }

    @Test
    void testValidateBulkRescheduleRequestWithValidData() {
        BulkReScheduleHearingRequest request = new BulkReScheduleHearingRequest();
        BulkReschedulingOfHearings bulkRescheduling = new BulkReschedulingOfHearings();
        bulkRescheduling.setJudgeId("judgeId");
        bulkRescheduling.setStartTime(LocalDateTime.now().plusDays(1));
        bulkRescheduling.setEndTime(LocalDateTime.now().plusDays(2));
        bulkRescheduling.setScheduleAfter(LocalDate.now().plusDays(1));
        request.setBulkRescheduling(bulkRescheduling);
        validator.validateBulkRescheduleRequest(request);
    }
}

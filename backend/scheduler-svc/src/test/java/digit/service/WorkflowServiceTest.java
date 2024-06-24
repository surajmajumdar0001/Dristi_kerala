package digit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.models.coremodels.*;
import digit.repository.ServiceRequestRepository;
import digit.web.models.*;
import digit.web.models.enums.Status;
import org.egov.common.contract.models.RequestInfoWrapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.workflow.BusinessService;
import org.egov.common.contract.workflow.BusinessServiceResponse;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkflowServiceTest {

    @Mock
    private ObjectMapper mapper;

    @Mock
    private ServiceRequestRepository repository;

    @Mock
    private Configuration config;

    @InjectMocks
    private WorkflowService workflowService;

    @Test
    public void testUpdateWorkflowStatus() {
        ReScheduleHearingRequest reScheduleHearingRequest = new ReScheduleHearingRequest();
        RequestInfo requestInfo = new RequestInfo();
        ReScheduleHearing reScheduleHearing = new ReScheduleHearing();
        reScheduleHearing.setTenantId("tenantId");
        reScheduleHearing.setRescheduledRequestId("rescheduledRequestId");
        Workflow workflow = new Workflow();
        workflow.setAction("RESCHEDULE");
        reScheduleHearing.setWorkflow(workflow);
        reScheduleHearingRequest.setRequestInfo(requestInfo);
        reScheduleHearingRequest.setReScheduleHearing(Collections.singletonList(reScheduleHearing));

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setState(new State());
        processInstance.getState().setApplicationStatus("RESCHEDULED");

        when(repository.fetchResult(any(StringBuilder.class), any(ProcessInstanceRequest.class)))
                .thenReturn(new Object());
        when(mapper.convertValue(any(), eq(ProcessInstanceResponse.class)))
                .thenReturn(new ProcessInstanceResponse());

        workflowService.updateWorkflowStatus(reScheduleHearingRequest);

        assertEquals(Status.RE_SCHEDULED, reScheduleHearing.getStatus());
    }

    @Test
    public void testCallWorkFlow() {
        ProcessInstanceRequest workflowReq = new ProcessInstanceRequest();
        ProcessInstance processInstance = new ProcessInstance();
        State state = new State();
        state.setApplicationStatus("RESCHEDULED");
        processInstance.setState(state);
        ProcessInstanceResponse response = new ProcessInstanceResponse();

        when(config.getWfHost()).thenReturn("http://localhost");
        when(config.getWfTransitionPath()).thenReturn("/workflow/v1/transition");
        when(repository.fetchResult(any(StringBuilder.class), any(ProcessInstanceRequest.class)))
                .thenReturn(new Object());
        when(mapper.convertValue(any(), eq(ProcessInstanceResponse.class)))
                .thenReturn(response);

        State result = workflowService.callWorkFlow(workflowReq);

        assertEquals("RESCHEDULED", result.getApplicationStatus());
    }

//    @Test
//    public void testGetProcessInstanceForHearingReScheduler() {
//        ReScheduleHearing application = new ReScheduleHearing();
//        application.setTenantId("tenantId");
//        application.setRescheduledRequestId("rescheduledRequestId");
//        application.setJudgeId("judgeId");
//        Workflow workflow = new Workflow();
//        workflow.setAction("RESCHEDULE");
//        workflow.setComment("comment");
//        workflow.setDocuments(Collections.emptyList());
//        workflow.setAssignees(Collections.singletonList("user1"));
//        application.setWorkflow(workflow);
//
//        RequestInfo requestInfo = new RequestInfo();
//        ProcessInstance processInstance = workflowService.getProcessInstanceForHearingReScheduler(application, requestInfo);
//
//        assertEquals("rescheduledRequestId", processInstance.getBusinessId());
//        assertEquals("RESCHEDULE", processInstance.getAction());
//        assertEquals("reschedule-hearing-services", processInstance.getModuleName());
//        assertEquals("tenantId", processInstance.getTenantId());
//        assertEquals("RESCHEDULER", processInstance.getBusinessService());
//        assertEquals("comment", processInstance.getComment());
//        assertEquals(1, processInstance.getAssignes().size());
//        assertEquals("user1", processInstance.getAssignes().get(0).getUuid());
//    }

    @Test
    void testGetCurrentWorkflow() {
        RequestInfo requestInfo = new RequestInfo();
        String tenantId = "tenantId";
        String businessId = "businessId";
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setId("processId");
        ProcessInstanceResponse response = new ProcessInstanceResponse();

        when(config.getWfHost()).thenReturn("http://localhost");
        when(config.getWfProcessInstanceSearchPath()).thenReturn("/workflow/v1/process/_search");
        when(repository.fetchResult(any(StringBuilder.class), any(RequestInfoWrapper.class)))
                .thenReturn(new Object());
        when(mapper.convertValue(any(), eq(ProcessInstanceResponse.class)))
                .thenReturn(response);

        ProcessInstance result = workflowService.getCurrentWorkflow(requestInfo, tenantId, businessId);

        assertEquals("processId", result.getId());
    }

    @Test
    void testGetCurrentWorkflow_Exception() {
        RequestInfo requestInfo = new RequestInfo();
        String tenantId = "tenantId";
        String businessId = "businessId";

        when(config.getWfHost()).thenReturn("http://localhost");
        when(config.getWfProcessInstanceSearchPath()).thenReturn("/workflow/v1/process/_search");
        when(repository.fetchResult(any(StringBuilder.class), any(RequestInfoWrapper.class)))
                .thenThrow(new CustomException("SERVICE_CALL_EXCEPTION", "Failed to fetch workflow from workflow service"));

        assertThrows(CustomException.class, () -> workflowService.getCurrentWorkflow(requestInfo, tenantId, businessId));
    }

//    @Test
//    void testGetBusinessService() {
//        ReScheduleHearing application = new ReScheduleHearing();
//        application.setTenantId("tenantId");
//        application.setJudgeId("judgeId");
//
//        BusinessService businessService = new BusinessService();
//        businessService.setBusinessService("RESCHEDULER");
//        BusinessServiceResponse response = new BusinessServiceResponse();
//
//        when(config.getWfHost()).thenReturn("http://localhost");
//        when(config.getWfBusinessServiceSearchPath()).thenReturn("/workflow/v1/businessservice/_search");
//        when(repository.fetchResult(any(StringBuilder.class), any(RequestInfoWrapper.class)))
//                .thenReturn(new Object());
//        when(mapper.convertValue(any(), eq(BusinessServiceResponse.class)))
//                .thenReturn(response);
//
//        BusinessService result = workflowService.getBusinessService(application, new RequestInfo());
//
//        assertEquals("RESCHEDULER", result.getBusinessService());
//    }

//    @Test
//    void testGetBusinessService_Exception() {
//        ReScheduleHearing application = new ReScheduleHearing();
//        application.setTenantId("tenantId");
//        application.setJudgeId("judgeId");
//
//        when(config.getWfHost()).thenReturn("http://localhost");
//        when(config.getWfBusinessServiceSearchPath()).thenReturn("/workflow/v1/businessservice/_search");
//        when(repository.fetchResult(any(StringBuilder.class), any(RequestInfoWrapper.class)))
//                .thenThrow(new CustomException("SERVICE_CALL_EXCEPTION", "Failed to fetch business service from workflow service"));
//
//        assertThrows(CustomException.class, () -> workflowService.getBusinessService(application, new RequestInfo()));
//    }

//    @Test
//    void testGetSearchURLWithParams() {
//        String tenantId = "tenantId";
//        String businessService = "RESCHEDULER";
//
//        when(config.getWfHost()).thenReturn("http://localhost");
//        when(config.getWfBusinessServiceSearchPath()).thenReturn("/workflow/v1/businessservice/_search");
//
//        StringBuilder result = workflowService.getSearchURLWithParams(tenantId, businessService);
//
//        assertEquals("http://localhost/workflow/v1/businessservice/_search?tenantId=tenantId&businessServices=RESCHEDULER", result.toString());
//    }
}

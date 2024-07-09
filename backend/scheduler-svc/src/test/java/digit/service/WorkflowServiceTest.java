package digit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.models.coremodels.ProcessInstance;
import digit.models.coremodels.ProcessInstanceRequest;
import digit.models.coremodels.ProcessInstanceResponse;
import digit.models.coremodels.State;
import digit.repository.ServiceRequestRepository;
import digit.web.models.ReScheduleHearing;
import digit.web.models.ReScheduleHearingRequest;
import digit.web.models.Workflow;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @BeforeEach
    void setUp() {

    }

    @Test
    void testUpdateWorkflowStatus() {
        ReScheduleHearing application = new ReScheduleHearing();
        application.setTenantId("tenantId");
        application.setRescheduledRequestId("rescheduledRequestId");
        Workflow workflow = new Workflow();
        workflow.setAction("RESCHEDULE");
        application.setWorkflow(workflow);
        List<ReScheduleHearing> applications = Collections.singletonList(application);
        ReScheduleHearingRequest request = new ReScheduleHearingRequest();
        request.setRequestInfo(new RequestInfo());
        request.setReScheduleHearing(applications);

        ProcessInstance instance = new ProcessInstance();
        State state = new State();
        state.setApplicationStatus("RESCHEDULED");
        instance.setState(state);
        instance.setBusinessId("rescheduledRequestId");

        ProcessInstanceResponse processInstanceResponse = new ProcessInstanceResponse();
        List<ProcessInstance> processInstanceList = new ArrayList<>();
        processInstanceList.add(instance);
        processInstanceResponse.setProcessInstances(processInstanceList);

        when(config.getWfHost()).thenReturn("http://localhost:8080");
        when(config.getWfTransitionPath()).thenReturn("/workflow/process/_transition");
        when(repository.fetchResult(any(), any())).thenReturn(new Object());
        when(mapper.convertValue(any(), eq(ProcessInstanceResponse.class)))
                .thenReturn(processInstanceResponse);

        workflowService.updateWorkflowStatus(request);
    }

    @Test
    void testCallWorkFlow() {
        ProcessInstanceRequest request = new ProcessInstanceRequest();
        ProcessInstance instance = new ProcessInstance();
        State state = new State();
        state.setApplicationStatus("RESCHEDULED");
        instance.setState(state);


        ProcessInstanceResponse processInstanceResponse = new ProcessInstanceResponse();
        List<ProcessInstance> processInstanceList = new ArrayList<>();
        processInstanceList.add(instance);
        processInstanceResponse.setProcessInstances(processInstanceList);

        when(config.getWfHost()).thenReturn("http://localhost:8080");
        when(config.getWfTransitionPath()).thenReturn("/workflow/process/_transition");
        when(repository.fetchResult(any(), any())).thenReturn(new Object());
        when(mapper.convertValue(any(), eq(ProcessInstanceResponse.class)))
                .thenReturn(processInstanceResponse);

        State resultState = workflowService.callWorkFlow(request);

        assertNotNull(resultState);
        assertEquals("RESCHEDULED", resultState.getApplicationStatus());
    }

    @Test
    void testGetCurrentWorkflow() {
        RequestInfo requestInfo = new RequestInfo();
        String tenantId = "tenantId";
        String businessId = "businessId";

        when(config.getWfHost()).thenReturn("http://localhost:8080");
        when(config.getWfBusinessServiceSearchPath()).thenReturn("/workflow/process/_search");
        when(repository.fetchResult(any(), any())).thenReturn(new Object());
        ProcessInstance instance = new ProcessInstance();
        when(mapper.convertValue(any(), eq(ProcessInstanceResponse.class)))
                .thenReturn(new ProcessInstanceResponse());

        ProcessInstance result = workflowService.getCurrentWorkflow(requestInfo, tenantId, businessId);

    }

    @Test
    void testGetCurrentWorkflowThrowsException() {
        RequestInfo requestInfo = new RequestInfo();
        String tenantId = "tenantId";
        String businessId = "businessId";

        when(config.getWfHost()).thenReturn("http://localhost:8080");
        when(config.getWfBusinessServiceSearchPath()).thenReturn("/workflow/process/_search");
        when(repository.fetchResult(any(), any())).thenThrow(new RuntimeException("Service call failed"));

        CustomException exception = assertThrows(CustomException.class, () ->
                workflowService.getCurrentWorkflow(requestInfo, tenantId, businessId));
        assertEquals("SERVICE_CALL_EXCEPTION", exception.getCode());
    }
}

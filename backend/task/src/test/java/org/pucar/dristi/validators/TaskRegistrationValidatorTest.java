package org.pucar.dristi.validators;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pucar.dristi.config.Configuration;
import org.pucar.dristi.repository.TaskRepository;
import org.pucar.dristi.service.TaskService;
import org.pucar.dristi.util.MdmsUtil;
import org.pucar.dristi.util.OrderUtil;
import org.pucar.dristi.web.models.Task;
import org.pucar.dristi.web.models.TaskExists;
import org.pucar.dristi.web.models.TaskRequest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.pucar.dristi.config.ServiceConstants.CREATE_TASK_ERR;
import static org.pucar.dristi.config.ServiceConstants.UPDATE_TASK_ERR;

@ExtendWith(MockitoExtension.class)
public class TaskRegistrationValidatorTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private TaskService taskService;

    @Mock
    private MdmsUtil mdmsUtil;

    @Mock
    private Configuration config;

    @Mock
    private OrderUtil orderUtil;

    @InjectMocks
    private TaskRegistrationValidator validator;

    private TaskRequest taskRequest;
    private Task task;
    private RequestInfo requestInfo;
    private User userInfo;

    @BeforeEach
    void setUp() {
        userInfo = User.builder().uuid("user-uuid").build();
        requestInfo = RequestInfo.builder().userInfo(userInfo).build();
        task = new Task();
        task.setTenantId("tenant-id");
        taskRequest = new TaskRequest();
        taskRequest.setRequestInfo(requestInfo);
        taskRequest.setTask(task);
    }

    @Test
    void testvalidateTaskRegistrationMissingTenantId() {
        task.setTenantId(null);
        CustomException exception = assertThrows(CustomException.class, () -> validator.validateTaskRegistration(taskRequest));
        assertEquals(CREATE_TASK_ERR, exception.getCode());
        assertEquals("tenantId is mandatory for creating task", exception.getMessage());
    }

    @Test
    void testvalidateTaskRegistrationMissingUserInfo() {
        taskRequest.getRequestInfo().setUserInfo(null);
        CustomException exception = assertThrows(CustomException.class, () -> validator.validateTaskRegistration(taskRequest));
        assertEquals(CREATE_TASK_ERR, exception.getCode());
        assertEquals("User info is mandatory for creating task", exception.getMessage());
    }

    @Test
    void testvalidateTaskRegistrationMissingTaskType() {
        task.setTaskType(null);
        CustomException exception = assertThrows(CustomException.class, () -> validator.validateTaskRegistration(taskRequest));
        assertEquals(CREATE_TASK_ERR, exception.getCode());
        assertEquals("Task type is mandatory for creating task", exception.getMessage());
    }

    @Test
    void testvalidateTaskRegistrationInvalidCaseDetails() {
        task.setTaskType("task-type");
        task.setCreatedDate(LocalDate.parse("2024-01-01"));

        CustomException exception = assertThrows(CustomException.class, () -> validator.validateTaskRegistration(taskRequest));
        assertEquals(CREATE_TASK_ERR, exception.getCode());
        assertEquals("Invalid order ID", exception.getMessage());
    }

    @Test
    void testValidateApplicationExistenceSuccess() {
        task.setId(UUID.randomUUID());
        when(repository.checkTaskExists(any())).thenReturn(TaskExists.builder().exists(true).build());

        boolean result = validator.validateApplicationExistence(task, requestInfo);

        assertTrue(result);
        verify(repository, times(1)).checkTaskExists(any());
    }

    @Test
    void testValidateApplicationExistenceMissingTenantId() {
        task.setTenantId(null);
        CustomException exception = assertThrows(CustomException.class, () -> validator.validateApplicationExistence(task, requestInfo));
        assertEquals(UPDATE_TASK_ERR, exception.getCode());
        assertEquals("tenantId is mandatory for updating task", exception.getMessage());
    }

    @Test
    void testValidateApplicationExistenceMissingUserInfo() {
        requestInfo.setUserInfo(null);
        CustomException exception = assertThrows(CustomException.class, () -> validator.validateApplicationExistence(task, requestInfo));
        assertEquals(UPDATE_TASK_ERR, exception.getCode());
        assertEquals("user info is mandatory for creating task", exception.getMessage());
    }

    @Test
    void testValidateApplicationExistenceNoExistingApplications() {
        task.setId(UUID.randomUUID());
        when(repository.checkTaskExists(any())).thenReturn(TaskExists.builder().exists(false).build());

        boolean result = validator.validateApplicationExistence(task, requestInfo);

        assertFalse(result);
        verify(repository, times(1)).checkTaskExists(any());
    }
}

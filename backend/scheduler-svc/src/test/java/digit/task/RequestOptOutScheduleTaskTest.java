package digit.task;

import digit.config.Configuration;
import digit.kafka.Producer;
import digit.repository.ReScheduleRequestRepository;
import digit.repository.RescheduleRequestOptOutRepository;
import digit.web.models.*;
import digit.web.models.enums.Status;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestOptOutScheduleTaskTest {

    @Mock
    private ReScheduleRequestRepository reScheduleRepository;

    @Mock
    private RescheduleRequestOptOutRepository requestOptOutRepository;

    @Mock
    private Producer producer;

    @Mock
    private Configuration config;

    @InjectMocks
    private RequestOptOutScheduleTask requestOptOutScheduleTask;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(requestOptOutScheduleTask, "reScheduleRepository", reScheduleRepository);
        ReflectionTestUtils.setField(requestOptOutScheduleTask, "requestOptOutRepository", requestOptOutRepository);
        ReflectionTestUtils.setField(requestOptOutScheduleTask, "producer", producer);
        ReflectionTestUtils.setField(requestOptOutScheduleTask, "config", config);
    }

    @Test
    void updateAvailableDatesFromOptOuts_success() {
        // Mock configuration
        when(config.getOptOutDueDate()).thenReturn(7L);
        when(config.getEgovStateTenantId()).thenReturn("tenantId");
        when(config.getUpdateRescheduleRequestTopic()).thenReturn("topic");

        // Mock data
        Long dueDate = 2L;
        ReScheduleHearing reScheduleHearing = new ReScheduleHearing();
        reScheduleHearing.setJudgeId("judgeId");
        reScheduleHearing.setCaseId("caseId");
        reScheduleHearing.setRescheduledRequestId("rescheduledRequestId");
        reScheduleHearing.setTenantId("tenantId");
        reScheduleHearing.setSuggestedDates(List.of(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)));
        reScheduleHearing.setAvailableDates(List.of(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)));
        reScheduleHearing.setStatus(Status.APPROVED);

        when(reScheduleRepository.getReScheduleRequest(any(), any(), any()))
                .thenReturn(List.of(reScheduleHearing));

        OptOut optOut = new OptOut();
        optOut.setOptoutDates(List.of(LocalDate.now().plusDays(1)));

        when(requestOptOutRepository.getOptOut(any(), any(), any()))
                .thenReturn(List.of(optOut));

        // Execute method
        requestOptOutScheduleTask.updateAvailableDatesFromOptOuts();

        // Verify interactions
        verify(reScheduleRepository, times(1)).getReScheduleRequest(any(), any(), any());
        verify(requestOptOutRepository, times(1)).getOptOut(any(), any(), any());
        verify(producer, times(1)).push(eq("topic"), any());

        // Assert changes
        assertEquals(Status.REVIEW, reScheduleHearing.getStatus());
        assertEquals(Collections.singletonList(LocalDate.now().plusDays(2)), reScheduleHearing.getAvailableDates());
    }

    @Test
    void updateAvailableDatesFromOptOuts_exception() {
        // Mock configuration
        when(config.getOptOutDueDate()).thenReturn(7L);
        when(config.getEgovStateTenantId()).thenReturn("tenantId");

        // Mock exception
        when(reScheduleRepository.getReScheduleRequest(any(), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // Execute method and assert exception
        CustomException exception = assertThrows(CustomException.class, () -> requestOptOutScheduleTask.updateAvailableDatesFromOptOuts());
        assertEquals("DK_SH_APP_ERR", exception.getCode());
        assertEquals("Error in setting available dates.", exception.getMessage());

        // Verify interactions
        verify(reScheduleRepository, times(1)).getReScheduleRequest(any(), any(), any());
        verify(requestOptOutRepository, never()).getOptOut(any(), any(), any());
        verify(producer, never()).push(any(), any());
    }
}

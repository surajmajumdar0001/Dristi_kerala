package digit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.helper.DefaultMasterDataHelper;
import digit.kafka.Producer;
import digit.repository.ReScheduleRequestRepository;
import digit.util.MdmsUtil;
import digit.web.models.*;
import digit.web.models.enums.Status;
import net.minidev.json.JSONArray;
import org.egov.common.contract.request.RequestInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HearingSchedulerTest {

    @Mock
    private Producer producer;

    @Mock
    private HearingService hearingService;

    @Mock
    private DefaultMasterDataHelper helper;

    @InjectMocks
    private HearingScheduler hearingScheduler;


    @Test
    void testScheduleHearingForApprovalStatusWithScheduleAction() {
        ReScheduleHearing hearing1 = new ReScheduleHearing();
        hearing1.setHearingBookingId("1");
        hearing1.setScheduleDate(LocalDate.now());
        Workflow workflow1 = new Workflow();
        workflow1.setAction("SCHEDULE");
        hearing1.setWorkflow(workflow1);

        ReScheduleHearing hearing2 = new ReScheduleHearing();
        hearing2.setHearingBookingId("2");
        hearing2.setScheduleDate(LocalDate.now());
        Workflow workflow2 = new Workflow();
        workflow2.setAction("APPROVE");
        hearing2.setWorkflow(workflow2);

        List<ReScheduleHearing> hearingList = Arrays.asList(hearing1, hearing2);
        ReScheduleHearingRequest reScheduleHearingRequest = new ReScheduleHearingRequest();
        reScheduleHearingRequest.setReScheduleHearing(hearingList);
        reScheduleHearingRequest.setRequestInfo(new RequestInfo());

        ScheduleHearing scheduleHearing1 = ScheduleHearing.builder().hearingBookingId("1").build();
        ScheduleHearing scheduleHearing2 = ScheduleHearing.builder().hearingBookingId("2").build();
        List<ScheduleHearing> scheduleHearings = Arrays.asList(scheduleHearing1, scheduleHearing2);

        MdmsSlot mdmsSlot = new MdmsSlot();
        mdmsSlot.setSlotDuration(60);
        List<MdmsSlot> defaultSlots = Collections.singletonList(mdmsSlot);

        MdmsHearing mdmsHearing = new MdmsHearing();
        mdmsHearing.setHearingType("type");
        List<MdmsHearing> defaultHearings = Collections.singletonList(mdmsHearing);

        Map<String, MdmsHearing> hearingTypeMap = new HashMap<>();
        hearingTypeMap.put("type", mdmsHearing);

        when(hearingService.search(any(HearingSearchRequest.class), any(), any())).thenReturn(scheduleHearings);
        when(helper.getDataFromMDMS(eq(MdmsSlot.class), anyString())).thenReturn(defaultSlots);
        when(helper.getDataFromMDMS(eq(MdmsHearing.class), anyString())).thenReturn(defaultHearings);

        hearingScheduler.scheduleHearingForApprovalStatus(reScheduleHearingRequest);

        verify(producer, times(1)).push(eq("schedule-hearing-to-block-calendar"), any());
        verify(hearingService, times(1)).search(any(HearingSearchRequest.class), any(), any());
        verify(hearingService, times(1)).updateBulk(any(ScheduleHearingRequest.class), eq(defaultSlots), eq(hearingTypeMap));
    }

    @Test
    void testScheduleHearingForApprovalStatusWithNoScheduleAction() {
        ReScheduleHearing hearing = new ReScheduleHearing();
        Workflow workflow = new Workflow();
        workflow.setAction("APPROVE");
        hearing.setWorkflow(workflow);

        ReScheduleHearingRequest reScheduleHearingRequest = new ReScheduleHearingRequest();
        reScheduleHearingRequest.setReScheduleHearing(Collections.singletonList(hearing));
        reScheduleHearingRequest.setRequestInfo(new RequestInfo());

        hearingScheduler.scheduleHearingForApprovalStatus(reScheduleHearingRequest);

        verify(producer, times(1)).push(eq("schedule-hearing-to-block-calendar"), any());
        verify(hearingService, never()).search(any(), any(), any());
        verify(hearingService, never()).updateBulk(any(), any(), any());
    }

    @Test
    void testScheduleHearingForApprovalStatusWithException() {
        ReScheduleHearing hearing = new ReScheduleHearing();
        Workflow workflow = new Workflow();
        workflow.setAction("SCHEDULE");
        hearing.setWorkflow(workflow);

        ReScheduleHearingRequest reScheduleHearingRequest = new ReScheduleHearingRequest();
        reScheduleHearingRequest.setReScheduleHearing(Collections.singletonList(hearing));
        reScheduleHearingRequest.setRequestInfo(new RequestInfo());

        doThrow(new RuntimeException("Test Exception")).when(hearingService).search(any(HearingSearchRequest.class), any(), any());

        hearingScheduler.scheduleHearingForApprovalStatus(reScheduleHearingRequest);

        verify(producer, never()).push(eq("schedule-hearing-to-block-calendar"), any());
        verify(hearingService, times(1)).search(any(), any(), any());
        verify(hearingService, never()).updateBulk(any(), any(), any());
    }

    @Test
    void testScheduleHearingForApprovalStatusWithEmptyRequest() {
        ReScheduleHearingRequest reScheduleHearingRequest = new ReScheduleHearingRequest();
        reScheduleHearingRequest.setReScheduleHearing(Collections.emptyList());
        reScheduleHearingRequest.setRequestInfo(new RequestInfo());

        hearingScheduler.scheduleHearingForApprovalStatus(reScheduleHearingRequest);

        verify(producer, never()).push(anyString(), any());
        verify(hearingService, never()).search(any(), any(), any());
        verify(hearingService, never()).updateBulk(any(), any(), any());
    }
}


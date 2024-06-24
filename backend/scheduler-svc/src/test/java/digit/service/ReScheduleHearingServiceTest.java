package digit.service;

import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.enrichment.ReScheduleRequestEnrichment;
import digit.helper.DefaultMasterDataHelper;
import digit.kafka.Producer;
import digit.repository.ReScheduleRequestRepository;
import digit.validator.ReScheduleRequestValidator;
import digit.web.models.*;
import digit.web.models.enums.Status;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReScheduleHearingServiceTest {

    @InjectMocks
    private ReScheduleHearingService reScheduleHearingService;


    @Mock
    private ReScheduleRequestRepository repository;

    @Mock
    private ReScheduleRequestValidator validator;

    @Mock
    private ReScheduleRequestEnrichment enrichment;

    @Mock
    private Producer producer;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private HearingService hearingService;

    @Mock
    private CalendarService calendarService;

    @Mock
    private HearingScheduler hearingScheduler;

    @Mock
    private ServiceConstants serviceConstants;

    @Mock
    private DefaultMasterDataHelper helper;

    private ReScheduleHearingRequest reScheduleHearingRequest;
    private BulkReScheduleHearingRequest bulkReScheduleHearingRequest;
    private RequestInfo requestInfo;

    @BeforeEach
    void setUp() {
        requestInfo = new RequestInfo();
        User user = User.builder().uuid("user-uuid").tenantId("tenant-id").build();
        requestInfo.setUserInfo(user);

        ReScheduleHearing reScheduleHearing = ReScheduleHearing.builder()
                .hearingBookingId("hearingBookingId")
                .judgeId("judgeId")
                .caseId("caseId")
                .tenantId("tenantId")
                .requesterId("requesterId")
                .workflow(Workflow.builder().action("ACTION").build())
                .actionComment("comment")
                .reason("reason")
                .build();

        reScheduleHearingRequest = ReScheduleHearingRequest.builder()
                .reScheduleHearing(List.of(reScheduleHearing))
                .requestInfo(requestInfo)
                .build();

        bulkReScheduleHearingRequest = BulkReScheduleHearingRequest.builder()
                .bulkRescheduling(BulkReschedulingOfHearings.builder()
                        .judgeId("judgeId")
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now().plusHours(1))
                        .scheduleAfter(LocalDate.now())
                        .build())
                .requestInfo(requestInfo)
                .build();
    }

    @Test
    public void testCreate() {
        doNothing().when(validator).validateRescheduleRequest(any());
        doNothing().when(enrichment).enrichRescheduleRequest(any());
        doNothing().when(workflowService).updateWorkflowStatus(any());
        doNothing().when(producer).push(anyString(), any());

        List<ReScheduleHearing> result = reScheduleHearingService.create(reScheduleHearingRequest);

        assertEquals(1, result.size());
        verify(validator, times(1)).validateRescheduleRequest(any());
        verify(enrichment, times(1)).enrichRescheduleRequest(any());
        verify(workflowService, times(1)).updateWorkflowStatus(any());
        verify(producer, times(1)).push(anyString(), any());
    }

    @Test
    public void testUpdate() {
        when(validator.validateExistingApplication(any())).thenReturn(reScheduleHearingRequest.getReScheduleHearing());
        doNothing().when(enrichment).enrichRequestOnUpdate(any(), any());
        doNothing().when(workflowService).updateWorkflowStatus(any());
        doNothing().when(hearingScheduler).scheduleHearingForApprovalStatus(any());
        doNothing().when(producer).push(anyString(), any());

        List<ReScheduleHearing> result = reScheduleHearingService.update(reScheduleHearingRequest);

        assertEquals(1, result.size());
        verify(validator, times(1)).validateExistingApplication(any());
        verify(enrichment, times(1)).enrichRequestOnUpdate(any(), any());
        verify(workflowService, times(1)).updateWorkflowStatus(any());
        verify(hearingScheduler, times(1)).scheduleHearingForApprovalStatus(any());
        verify(producer, times(1)).push(anyString(), any());
    }

    @Test
    public void testSearch() {
        ReScheduleHearingReqSearchRequest searchRequest = ReScheduleHearingReqSearchRequest.builder()
                .criteria(ReScheduleHearingReqSearchCriteria.builder().build())
                .build();

        when(repository.getReScheduleRequest(any(), any(), any())).thenReturn(List.of(reScheduleHearingRequest.getReScheduleHearing().get(0)));

        List<ReScheduleHearing> result = reScheduleHearingService.search(searchRequest, 10, 0);

        assertEquals(1, result.size());
        verify(repository, times(1)).getReScheduleRequest(any(), any(), any());
    }

    @Test
    public void testBulkReschedule() {
        doNothing().when(validator).validateBulkRescheduleRequest(any());

        MdmsSlot slot1 = new MdmsSlot();
        slot1.setSlotDuration(60);
        MdmsSlot slot2 = new MdmsSlot();
        slot2.setSlotDuration(60);
        when(helper.getDataFromMDMS(eq(MdmsSlot.class), anyString())).thenReturn(List.of(slot1, slot2));

        MdmsHearing hearing1 = new MdmsHearing();
        hearing1.setHearingType("type1");
        hearing1.setHearingTime(60);
        when(helper.getDataFromMDMS(eq(MdmsHearing.class), anyString())).thenReturn(List.of(hearing1));

        List<ScheduleHearing> hearings = List.of(
                ScheduleHearing.builder().hearingBookingId("hearingBookingId1").status(Status.SCHEDULED).build()
        );
        when(hearingService.search(any(), any(), any())).thenReturn(hearings);

        List<AvailabilityDTO> availability = List.of(
                AvailabilityDTO.builder().date(LocalDate.now().toString()).occupiedBandwidth(0.0).build()
        );
        when(calendarService.getJudgeAvailability(any())).thenReturn(availability);

        doNothing().when(producer).push(anyString(), any());
        doNothing().when(hearingService).updateBulk(any(), any(), any());

        List<ReScheduleHearing> result = reScheduleHearingService.bulkReschedule(bulkReScheduleHearingRequest);

        assertEquals(1, result.size());
        verify(validator, times(1)).validateBulkRescheduleRequest(any());
        verify(helper, times(1)).getDataFromMDMS(eq(MdmsSlot.class), anyString());
        verify(helper, times(1)).getDataFromMDMS(eq(MdmsHearing.class), anyString());
        verify(hearingService, times(1)).search(any(), any(), any());
        verify(calendarService, times(1)).getJudgeAvailability(any());
        verify(producer, times(2)).push(anyString(), any());
        verify(hearingService, times(1)).updateBulk(any(), any(), any());
    }

//    @Test
//    void testCreateReschedulingRequest() {
//        List<ScheduleHearing> hearings = List.of(
//                ScheduleHearing.builder().hearingBookingId("hearingBookingId1").judgeId("judgeId1").caseId("caseId1").tenantId("tenantId1").build()
//        );
//
//        List<ReScheduleHearing> result = reScheduleHearingService.create(hearings, "requesterId");
//
//        assertEquals(1, result.size());
//        assertEquals("hearingBookingId1", result.get(0).getHearingBookingId());
//    }
}

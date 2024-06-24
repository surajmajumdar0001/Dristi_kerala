package digit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.kafka.Producer;
import digit.repository.ReScheduleRequestRepository;
import digit.util.CaseUtil;
import digit.web.models.*;
import digit.web.models.cases.CaseCriteria;
import digit.web.models.cases.SearchCaseRequest;
import digit.web.models.enums.Status;
import org.egov.common.contract.request.RequestInfo;
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
class OptOutConsumerServiceTest {

    @Mock
    private Producer producer;

    @Mock
    private ReScheduleRequestRepository repository;

    @Mock
    private Configuration configuration;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private HearingService hearingService;

    @Mock
    private RescheduleRequestOptOutService optOutService;

    @Mock
    private CaseUtil caseUtil;

    @InjectMocks
    private OptOutConsumerService optOutConsumerService;

    private HashMap<String, Object> record;
    private OptOutRequest optOutRequest;
    private List<OptOut> optOuts;
    private List<ScheduleHearing> hearingList;
    private List<ReScheduleHearing> reScheduleRequest;
    private RequestInfo requestInfo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        record = new HashMap<>();
        optOutRequest = new OptOutRequest();
        optOuts = new ArrayList<>();
        requestInfo = new RequestInfo();
        hearingList = new ArrayList<>();
        reScheduleRequest = new ArrayList<>();

        optOutRequest.setRequestInfo(new RequestInfo());
        optOutRequest.setOptOuts(List.of(
                OptOut.builder()
                        .rescheduleRequestId("rescheduleRequestId1")
                        .optoutDates(List.of(LocalDate.now()))
                        .caseId("caseId1")
                        .build()
        ));

        when(mapper.convertValue(record, OptOutRequest.class)).thenReturn(optOutRequest);
        when(repository.getReScheduleRequest(any(), any(), any())).thenReturn(reScheduleRequest);
    }

    @Test
    void testCheckAndScheduleHearingForOptOutWithValidData() {
        OptOut optOut = new OptOut();
        optOut.setRescheduleRequestId("rescheduleId");
        optOut.setOptoutDates(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        optOut.setCaseId("caseId");
        optOuts.add(optOut);

        ScheduleHearing hearing = new ScheduleHearing();
        hearing.setHearingBookingId("hearingId");
        hearing.setStatus(Status.BLOCKED);
        hearingList.add(hearing);

        ReScheduleHearing reScheduleHearing = new ReScheduleHearing();
        reScheduleHearing.setSuggestedDates(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        reScheduleHearing.setAvailableDates(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        reScheduleHearing.setStatus(Status.APPLIED);
        reScheduleRequest.add(reScheduleHearing);

        JsonNode representatives = mock(JsonNode.class);
        when(representatives.size()).thenReturn(1);
        when(caseUtil.getRepresentatives(any())).thenReturn(representatives);
        when(hearingService.search(any(HearingSearchRequest.class), any(), any())).thenReturn(hearingList);

        optOutConsumerService.checkAndScheduleHearingForOptOut(record);

        verify(hearingService, times(1)).search(any(HearingSearchRequest.class), any(), any());
        verify(hearingService, times(1)).update(any(ScheduleHearingRequest.class));
        verify(repository, times(1)).getReScheduleRequest(any(), any(), any());
        verify(producer, times(1)).push(anyString(), any());
    }

    @Test
    void testCheckAndScheduleHearingForOptOutWithException() {
        when(mapper.convertValue(record, OptOutRequest.class)).thenThrow(new RuntimeException("Test Exception"));

        optOutConsumerService.checkAndScheduleHearingForOptOut(record);

        verify(hearingService, never()).search(any(HearingSearchRequest.class), any(), any());
        verify(hearingService, never()).update(any(ScheduleHearingRequest.class));
        verify(repository, never()).getReScheduleRequest(any(), any(), any());
        verify(producer, never()).push(anyString(), any());
    }

    @Test
    void testCheckAndScheduleHearingForOptOutWithEmptyOptOutList() {
        optOutRequest.setOptOuts(Collections.emptyList());

        optOutConsumerService.checkAndScheduleHearingForOptOut(record);

        verify(hearingService, never()).search(any(HearingSearchRequest.class), any(), any());
        verify(hearingService, never()).update(any(ScheduleHearingRequest.class));
        verify(repository, never()).getReScheduleRequest(any(), any(), any());
        verify(producer, never()).push(anyString(), any());
    }

    @Test
    void testCheckAndScheduleHearingForOptOutWithNoHearingsToCancel() {
        OptOut optOut = new OptOut();
        optOut.setRescheduleRequestId("rescheduleId");
        optOut.setOptoutDates(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        optOut.setCaseId("caseId");
        optOuts.add(optOut);

        JsonNode representatives = mock(JsonNode.class);
        when(representatives.size()).thenReturn(1);
        when(caseUtil.getRepresentatives(any())).thenReturn(representatives);
        when(hearingService.search(any(HearingSearchRequest.class), any(), any())).thenReturn(Collections.emptyList());

        optOutConsumerService.checkAndScheduleHearingForOptOut(record);

        verify(hearingService, times(1)).search(any(HearingSearchRequest.class), any(), any());
        verify(hearingService, never()).update(any(ScheduleHearingRequest.class));
        verify(repository, never()).getReScheduleRequest(any(), any(), any());
        verify(producer, never()).push(anyString(), any());
    }

    @Test
    void testCheckAndScheduleHearingForOptOutWithRepresentativesMismatch() {
        OptOut optOut = new OptOut();
        optOut.setRescheduleRequestId("rescheduleId");
        optOut.setOptoutDates(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        optOut.setCaseId("caseId");
        optOuts.add(optOut);

        ScheduleHearing hearing = new ScheduleHearing();
        hearing.setHearingBookingId("hearingId");
        hearing.setStatus(Status.BLOCKED);
        hearingList.add(hearing);

        ReScheduleHearing reScheduleHearing = new ReScheduleHearing();
        reScheduleHearing.setSuggestedDates(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        reScheduleHearing.setAvailableDates(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(1)));
        reScheduleHearing.setStatus(Status.APPLIED);
        reScheduleRequest.add(reScheduleHearing);

        JsonNode representatives = mock(JsonNode.class);
        when(representatives.size()).thenReturn(2);
        when(caseUtil.getRepresentatives(any())).thenReturn(representatives);
        when(hearingService.search(any(HearingSearchRequest.class), any(), any())).thenReturn(hearingList);

        optOutConsumerService.checkAndScheduleHearingForOptOut(record);

        verify(hearingService, times(1)).search(any(HearingSearchRequest.class), any(), any());
        verify(hearingService, times(1)).update(any(ScheduleHearingRequest.class));
        verify(repository, times(1)).getReScheduleRequest(any(), any(), any());
        verify(producer, times(1)).push(anyString(), any());
    }
}

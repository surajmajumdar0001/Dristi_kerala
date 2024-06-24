package digit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.kafka.Producer;
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
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleHearingConsumerServiceTest {

    @Mock
    private Configuration configuration;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private CalendarService calendarService;

    @Mock
    private Producer producer;

    @Mock
    private HearingService hearingService;

    @Mock
    private CaseUtil caseUtil;

    @InjectMocks
    private ScheduleHearingConsumerService scheduleHearingConsumerService;

    @Test
    public void testUpdateRequestForBlockCalendar() {
        // Prepare test data
        HashMap<String, Object> record = new HashMap<>();
        record.put("key", "value");

        ReScheduleHearingRequest reScheduleHearingRequest = new ReScheduleHearingRequest();
        RequestInfo requestInfo = new RequestInfo();
        ReScheduleHearing reScheduleHearing = new ReScheduleHearing();
        reScheduleHearing.setTenantId("tenantId");
        reScheduleHearing.setCaseId("caseId");
        reScheduleHearing.setJudgeId("judgeId");
        reScheduleHearing.setAvailableAfter(LocalDate.now().plusDays(2));
        reScheduleHearing.setRowVersion(1);
        reScheduleHearingRequest.setRequestInfo(requestInfo);
        reScheduleHearingRequest.setReScheduleHearing(Collections.singletonList(reScheduleHearing));

        SearchCaseRequest searchCaseRequest = SearchCaseRequest.builder()
                .RequestInfo(requestInfo)
                .tenantId("kl")
                .criteria(Collections.singletonList(CaseCriteria.builder().caseId("caseId").build()))
                .build();

        JsonNode jsonNode = mock(JsonNode.class);
        Set<String> representativeIds = new HashSet<>(Arrays.asList("rep1", "rep2"));

        AvailabilityDTO availabilityDTO = new AvailabilityDTO();
        availabilityDTO.setDate("2024-06-25");
        List<AvailabilityDTO> availabilityDTOList = Collections.singletonList(availabilityDTO);

        ScheduleHearing scheduleHearing = new ScheduleHearing();
        scheduleHearing.setDate(LocalDate.now());
        scheduleHearing.setStartTime(LocalDateTime.now());
        scheduleHearing.setEndTime(LocalDateTime.now());

        List<ScheduleHearing> scheduleHearingList = Collections.singletonList(scheduleHearing);

        when(mapper.convertValue(record, ReScheduleHearingRequest.class)).thenReturn(reScheduleHearingRequest);
        when(caseUtil.getRepresentatives(searchCaseRequest)).thenReturn(jsonNode);
        when(caseUtil.getIdsFromJsonNodeArray(jsonNode)).thenReturn(representativeIds);
        when(configuration.getOptOutLimit()).thenReturn(2L);
        when(calendarService.getJudgeAvailability(any())).thenReturn(availabilityDTOList);
        when(hearingService.search(any(), any(), any())).thenReturn(scheduleHearingList);

        // Run the method under test
        scheduleHearingConsumerService.updateRequestForBlockCalendar(record);

        // Verify interactions
        verify(producer).push(eq(configuration.getUpdateRescheduleRequestTopic()), anyList());
    }

    @Test
    public void testUpdateRequestForBlockCalendar_Exception() {
        HashMap<String, Object> record = new HashMap<>();
        record.put("key", null);

        when(mapper.convertValue(record, ReScheduleHearingRequest.class)).thenThrow(new RuntimeException("Test Exception"));

        // Run the method under test
        scheduleHearingConsumerService.updateRequestForBlockCalendar(record);

        // Verify that the error is logged
        verify(mapper).convertValue(record, ReScheduleHearingRequest.class);
    }
}

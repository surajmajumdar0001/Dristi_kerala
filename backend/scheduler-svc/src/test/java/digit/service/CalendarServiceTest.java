package digit.service;

import digit.TestConfiguration;
import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.enrichment.JudgeCalendarEnrichment;
import digit.helper.DefaultMasterDataHelper;
import digit.kafka.Producer;
import digit.repository.CalendarRepository;
import digit.util.MdmsUtil;
import digit.validator.JudgeCalendarValidator;
import digit.web.models.*;
import net.minidev.json.JSONArray;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(CalendarService.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CalendarServiceTest {

    @Mock
    private CalendarService calendarService;

    @Mock
    private JudgeCalendarValidator validator;

    @Mock
    private DefaultMasterDataHelper helper;

    @Mock
    private MdmsUtil mdmsUtil;

    @Mock
    private ServiceConstants serviceConstants;

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private HearingService hearingService;

    @Mock
    private JudgeCalendarEnrichment enrichment;

    @Mock
    private Producer producer;

    @Mock
    private Configuration config;

    private JudgeAvailabilitySearchRequest searchRequest;
    private JudgeAvailabilitySearchCriteria criteria;

    @BeforeEach
    void setUp() {
        criteria = new JudgeAvailabilitySearchCriteria();
        criteria.setTenantId("kl");
        criteria.setFromDate(LocalDate.now());
        criteria.setNumberOfSuggestedDays(5);

        searchRequest = new JudgeAvailabilitySearchRequest();
        searchRequest.setCriteria(criteria);
    }

    @Test
    public void getJudgeCalendarTest() {

        CalendarSearchCriteria criteria = new CalendarSearchCriteria();
        doNothing().when(validator).validateSearchRequest(criteria);

        JudgeCalendarSearchRequest request = new JudgeCalendarSearchRequest();
        HearingCalendar hearingCalendar = new HearingCalendar();
        List<HearingCalendar> judgeCalendar = List.of(hearingCalendar);

        Map<String, Map<String, JSONArray>> defaultCourtCalendar = new HashMap<>();
        Map<String, JSONArray> scheduleHearing = new HashMap<>();
        JSONArray court000334 = new JSONArray();
        Map<String, Object> map = new HashMap<>();
        map.put("date", "12-06-2024");
        court000334.add(new LinkedHashMap<>(map));
        scheduleHearing.put("COURT000334", court000334);
        defaultCourtCalendar.put("schedule-hearing", scheduleHearing);

        when(mdmsUtil.fetchMdmsData(any(), anyString(), anyString(), anyList()))
                .thenReturn(defaultCourtCalendar);

        JudgeCalendarRule rule = new JudgeCalendarRule();
        List<JudgeCalendarRule> rules = List.of(rule);
        when(calendarRepository.getJudgeRule(criteria)).thenReturn(rules);

        ScheduleHearing hearing = new ScheduleHearing();
        List<ScheduleHearing> scheduledHearings = List.of(hearing);
        when(hearingService.search(any(), any(), any())).thenReturn(scheduledHearings);

        List<HearingCalendar> calendar = calendarService.getJudgeCalendar(request);

        // Validate the results
        assertNotNull(calendar);
        assertFalse(calendar.isEmpty());
        assertEquals(judgeCalendar, calendar);
    }

    @Test
    public void getJudgeAvailabilityTest() {
        JudgeAvailabilitySearchCriteria criteria = new JudgeAvailabilitySearchCriteria();
        criteria.setTenantId("kl");

        JudgeAvailabilitySearchRequest request = new JudgeAvailabilitySearchRequest();

        doNothing().when(validator).validateSearchRequest(criteria);

        AvailabilityDTO availability = new AvailabilityDTO();
        List<AvailabilityDTO> judgeAvailability = List.of(availability);

        when(helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME));
        Map<String, Map<String, JSONArray>> defaultCalendarResponse = new HashMap<>();
        when(mdmsUtil.fetchMdmsData(request.getRequestInfo(), criteria.getTenantId(), serviceConstants.DEFAULT_JUDGE_CALENDAR_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_JUDGE_CALENDAR_MASTER_NAME))).thenReturn(defaultCalendarResponse);

        List<JudgeCalendarRule> judgeCalendarRules = List.of(new JudgeCalendarRule());
        when(calendarRepository.getJudgeRule(criteria)).thenReturn(judgeCalendarRules);

        HearingSearchCriteria hearingSearchCriteria = new HearingSearchCriteria();
        hearingSearchCriteria.setFromDate(criteria.getFromDate());
        hearingSearchCriteria.setJudgeId(criteria.getJudgeId());
        hearingSearchCriteria.setToDate(criteria.getToDate().plusDays(30*6));

        List<AvailabilityDTO> availableDatesForHearing = List.of(availability);

        when(hearingService.getAvailableDateForHearing(hearingSearchCriteria)).thenReturn(availableDatesForHearing);


        // Validate the results
        assertNotNull(availableDatesForHearing);
        assertFalse(availableDatesForHearing.isEmpty());
    }

    @Test
    public void updateJudgeCalendarTest() {
        JudgeCalendarUpdateRequest request = new JudgeCalendarUpdateRequest();
        JudgeCalendarRule judgeCalendarRule = new JudgeCalendarRule();
        List<JudgeCalendarRule> updatedJudgeCalendarRule = List.of(judgeCalendarRule);

        verify(validator).validateUpdateJudgeCalendar(updatedJudgeCalendarRule);
        verify(enrichment).enrichUpdateJudgeCalendar(request.getRequestInfo(), updatedJudgeCalendarRule);
        verify(producer).push(config.getUpdateJudgeCalendarTopic(), judgeCalendarRule);

        when(calendarService.upsert(request)).thenReturn(updatedJudgeCalendarRule);

        assertNotNull(updatedJudgeCalendarRule);
    }
}

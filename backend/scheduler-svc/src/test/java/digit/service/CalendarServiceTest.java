package digit.service;

import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.enrichment.JudgeCalendarEnrichment;
import digit.helper.DefaultMasterDataHelper;
import digit.kafka.Producer;
import digit.repository.CalendarRepository;
import digit.util.MdmsUtil;
import digit.validator.JudgeCalendarValidator;
import digit.web.models.*;
import digit.web.models.enums.PeriodType;
import digit.web.models.enums.Status;
import net.minidev.json.JSONArray;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CalendarServiceTest {

    @InjectMocks
    private CalendarService calendarService;

    @Mock
    private JudgeCalendarValidator validator;

    @Mock
    private JudgeCalendarEnrichment enrichment;

    @Mock
    private Producer producer;

    @Mock
    private Configuration config;

    @Mock
    private MdmsUtil mdmsUtil;

    @Mock
    private ServiceConstants serviceConstants;

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private HearingService hearingService;

    @Mock
    private DefaultMasterDataHelper helper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetJudgeAvailability_success() {
        JudgeAvailabilitySearchRequest request = new JudgeAvailabilitySearchRequest();
        JudgeAvailabilitySearchCriteria criteria = new JudgeAvailabilitySearchCriteria();
        criteria.setFromDate(LocalDate.now());
        criteria.setJudgeId("JUDGE1");
        criteria.setTenantId("TENANT1");
        criteria.setCourtId("COURT1");
        criteria.setNumberOfSuggestedDays(5);
        request.setCriteria(criteria);

        MdmsSlot mdmsSlot = new MdmsSlot();
        mdmsSlot.setSlotDuration(60);
        List<MdmsSlot> defaultSlots = List.of(mdmsSlot);
        when(helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME)).thenReturn(defaultSlots);

        Map<String, Map<String, JSONArray>> defaultCalendarResponse = new HashMap<>();
        Map<String, JSONArray> innerMap = new HashMap<>();
        JSONArray jsonArray = new JSONArray();
        LinkedHashMap map = new LinkedHashMap();
        map.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        jsonArray.add(map);
        innerMap.put("COURT000334", jsonArray);
        defaultCalendarResponse.put("schedule-hearing", innerMap);

        when(mdmsUtil.fetchMdmsData(any(), any(), any(), any())).thenReturn(defaultCalendarResponse);

        List<JudgeCalendarRule> judgeCalendarRules = Collections.singletonList(JudgeCalendarRule.builder().date(LocalDate.now()).tenantId("tenant").judgeId("judge").build());
        when(calendarRepository.getJudgeRule(any())).thenReturn(judgeCalendarRules);

        List<AvailabilityDTO> availableDates = Collections.singletonList(new AvailabilityDTO(LocalDate.now().toString(), 1.0));
        when(hearingService.getAvailableDateForHearing(any())).thenReturn(availableDates);

        List<AvailabilityDTO> result = calendarService.getJudgeAvailability(request);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetJudgeAvailability_noAvailableDates() {
        JudgeAvailabilitySearchRequest request = new JudgeAvailabilitySearchRequest();
        JudgeAvailabilitySearchCriteria criteria = new JudgeAvailabilitySearchCriteria();
        criteria.setFromDate(LocalDate.now());
        criteria.setJudgeId("JUDGE1");
        criteria.setTenantId("TENANT1");
        criteria.setCourtId("COURT1");
        criteria.setNumberOfSuggestedDays(5);
        request.setCriteria(criteria);

        List<MdmsSlot> defaultSlots = Collections.singletonList(new MdmsSlot());
        when(helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME)).thenReturn(defaultSlots);

        Map<String, Map<String, JSONArray>> defaultCalendarResponse = new HashMap<>();
        Map<String, JSONArray> innerMap = new HashMap<>();
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(Collections.singletonMap("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        innerMap.put("COURT000334", jsonArray);
        defaultCalendarResponse.put("schedule-hearing", innerMap);

        when(mdmsUtil.fetchMdmsData(any(), any(), any(), any())).thenReturn(defaultCalendarResponse);

        List<JudgeCalendarRule> judgeCalendarRules = Collections.singletonList(JudgeCalendarRule.builder().date(LocalDate.now()).tenantId("tenant").judgeId("judge").build());
        when(calendarRepository.getJudgeRule(any())).thenReturn(judgeCalendarRules);

        List<AvailabilityDTO> availableDates = Collections.emptyList();
        when(hearingService.getAvailableDateForHearing(any())).thenReturn(availableDates);

        assertThrows(NullPointerException.class, () -> calendarService.getJudgeAvailability(request));
    }

    @Test
    void testGetJudgeCalendar_success() {
        JudgeCalendarSearchRequest request = new JudgeCalendarSearchRequest();
        CalendarSearchCriteria criteria = new CalendarSearchCriteria();
        criteria.setPeriodType(PeriodType.CURRENT_MONTH);
        criteria.setJudgeId("JUDGE1");
        criteria.setTenantId("TENANT1");
        criteria.setCourtId("COURT1");
        request.setCriteria(criteria);

        Map<String, Map<String, JSONArray>> defaultCalendarResponse = new HashMap<>();
        Map<String, JSONArray> innerMap = new HashMap<>();
        JSONArray jsonArray = new JSONArray();
        LinkedHashMap map = new LinkedHashMap();
        map.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        jsonArray.add(map);
        innerMap.put("COURT000334", jsonArray);
        defaultCalendarResponse.put("schedule-hearing", innerMap);

        when(mdmsUtil.fetchMdmsData(any(), any(), any(), any())).thenReturn(defaultCalendarResponse);

        List<JudgeCalendarRule> judgeCalendarRules = Collections.singletonList(new JudgeCalendarRule());
        when(calendarRepository.getJudgeRule(any())).thenReturn(judgeCalendarRules);

        List<ScheduleHearing> hearings = Collections.singletonList(new ScheduleHearing());
        when(hearingService.search(any(), any(), any())).thenReturn(hearings);

        List<HearingCalendar> result = calendarService.getJudgeCalendar(request);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testUpsert_success() {
        JudgeCalendarUpdateRequest request = new JudgeCalendarUpdateRequest();
        List<JudgeCalendarRule> rules = Collections.singletonList(new JudgeCalendarRule());
        request.setJudgeCalendarRule(rules);

        doNothing().when(validator).validateUpdateJudgeCalendar(any());
        doNothing().when(enrichment).enrichUpdateJudgeCalendar(any(), any());
        doNothing().when(producer).push(any(), any());

        List<JudgeCalendarRule> result = calendarService.upsert(request);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetFromAndToDateFromPeriodType_currentDate() {
        PeriodType periodType = PeriodType.CURRENT_DATE;
        Pair<LocalDate, LocalDate> result = calendarService.getFromAndToDateFromPeriodType(periodType);

        assertNotNull(result);
        assertEquals(LocalDate.now(), result.getKey());
        assertEquals(LocalDate.now(), result.getValue());
    }

    @Test
    void testGetFromAndToDateFromPeriodType_currentWeek() {
        PeriodType periodType = PeriodType.CURRENT_WEEK;
        Pair<LocalDate, LocalDate> result = calendarService.getFromAndToDateFromPeriodType(periodType);

        assertNotNull(result);
        assertEquals(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), result.getKey());
        assertEquals(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)), result.getValue());
    }

    @Test
    void testGetFromAndToDateFromPeriodType_currentMonth() {
        PeriodType periodType = PeriodType.CURRENT_MONTH;
        Pair<LocalDate, LocalDate> result = calendarService.getFromAndToDateFromPeriodType(periodType);

        assertNotNull(result);
        assertEquals(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), result.getKey());
        assertEquals(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), result.getValue());
    }

    @Test
    void testGetFromAndToDateFromPeriodType_currentYear() {
        PeriodType periodType = PeriodType.CURRENT_YEAR;
        Pair<LocalDate, LocalDate> result = calendarService.getFromAndToDateFromPeriodType(periodType);

        assertNotNull(result);
        assertEquals(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), result.getKey());
        assertEquals(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()), result.getValue());
    }
}

package digit.service;


import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.enrichment.JudgeCalendarEnrichment;
import digit.kafka.Producer;
import digit.repository.CalendarRepository;
import digit.util.MdmsUtil;
import digit.validator.JudgeCalendarValidator;
import digit.web.models.*;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Stream;

@Service
public class CalendarService {

    private final JudgeCalendarValidator validator;
    private final JudgeCalendarEnrichment enrichment;
    private final Producer producer;
    private Configuration config;

    @Autowired
    private MdmsUtil mdmsUtil;

    @Autowired
    private ServiceConstants serviceConstants;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private HearingService hearingService;


    @Autowired
    public CalendarService(JudgeCalendarValidator validator, JudgeCalendarEnrichment enrichment, Producer producer, Configuration config) {
        this.validator = validator;
        this.enrichment = enrichment;
        this.producer = producer;
        this.config = config;
    }


    public List<String> getJudgeAvailability(JudgeCalendarSearchRequest searchCriteriaRequest) {

        List<String> resultList = new ArrayList<>();
        HashMap<String, Boolean> dateMap = new HashMap<>();

        int calendarLength;
        int hearingLength;
        JudgeSearchCriteria criteria = searchCriteriaRequest.getCriteria();

        //  fetch mdms data of default calendar for court id
        Map<String, Map<String, JSONArray>> defaultCalendarResponse = mdmsUtil.fetchMdmsData(searchCriteriaRequest.getRequestInfo(), criteria.getTenantId(), serviceConstants.DEFAULT_JUDGE_CALENDAR_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_JUDGE_CALENDAR_MASTER_NAME));

        //  fetch judge calendar rule for next thirty days
        List<JudgeCalendarRule> judgeCalendarRule = calendarRepository.getJudgeRule(criteria);


        calendarLength = judgeCalendarRule.size();


        HearingSearchCriteria hearingSearchCriteria = HearingSearchCriteria.builder().fromDate(criteria.getFromDate()).judgeId(criteria.getJudgeId()).toDate(criteria.getFromDate().plusDays(30)).build();

        List<String> availableDateForHearing = hearingService.getAvailableDateForHearing(hearingSearchCriteria);
        hearingLength = availableDateForHearing.size();


        int loopLength = Math.max(calendarLength, hearingLength);  // need to add mdms as well

        for (int i = 0; i < loopLength; i++) {

            if (i < calendarLength) dateMap.put(judgeCalendarRule.get(i).getDate().toString(), false);
            if (i < hearingLength) dateMap.put(availableDateForHearing.get(i), true);

            //one more if condition for mdms
        }

        LocalDate endDate = criteria.getFromDate().plusDays(30);
        // check startDate in date map if its exits and value is true then add to the result list
        Stream.iterate(criteria.getFromDate(), startDate -> startDate.isBefore(endDate), startDate -> startDate.plusDays(1)).forEach(startDate -> {
            if (dateMap.containsKey(startDate.toString()) && dateMap.get(startDate.toString()))
                resultList.add(startDate.toString());
            else resultList.add(startDate.toString());
        });

        return resultList;


    }


    public List<HearingCalendar> getJudgeCalendar(JudgeCalendarSearchRequest searchCriteriaRequest) {

        List<HearingCalendar> calendar = new ArrayList<>();
        HashMap<LocalDate, List<ScheduleHearing>> dayHearingMap = new HashMap<>();
        HashMap<LocalDate, Object> leaveMap = new HashMap<>();

        JudgeSearchCriteria criteria = searchCriteriaRequest.getCriteria();

        //fetch mdms data of default calendar for court id and judge id
        Map<String, Map<String, JSONArray>> defaultCourtCalendar = mdmsUtil.fetchMdmsData(searchCriteriaRequest.getRequestInfo(), criteria.getTenantId(), serviceConstants.DEFAULT_JUDGE_CALENDAR_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_JUDGE_CALENDAR_MASTER_NAME));

        //fetch judge calendar rule
        List<JudgeCalendarRule> judgeCalendarRule = calendarRepository.getJudgeRule(criteria);
        for (JudgeCalendarRule calendarRule : judgeCalendarRule) {

            leaveMap.put(calendarRule.getDate(), calendarRule);

        }

        HearingSearchCriteria hearingSearchCriteria = getHearingSearchCriteriaFromJudgeSearch(criteria);
        // sort on the basis of start time
        List<ScheduleHearing> hearings = hearingService.getJudgeHearing(hearingSearchCriteria);

        hearings.forEach((hearing) -> {

            if (dayHearingMap.containsKey(hearing.getDate())) {
                dayHearingMap.get(hearing.getDate()).add(hearing);
            } else {
                dayHearingMap.put(hearing.getDate(), new ArrayList<>(Collections.singletonList(hearing)));
            }

        });

        //merge all three and return the calendar

        for (LocalDate start = hearingSearchCriteria.getFromDate(); start.isBefore(hearingSearchCriteria.getToDate()) || start.isEqual(hearingSearchCriteria.getToDate()); start = start.plusDays(1)) {
            List<ScheduleHearing> hearingOfaDay = dayHearingMap.getOrDefault(start, new ArrayList<>());

            HearingCalendar calendarOfDay = HearingCalendar.builder()
                    .judgeId(criteria.getJudgeId())
                    .isOnLeave(leaveMap.containsKey(start))
                    .isHoliday(leaveMap.containsKey(start))
                    .notes("notes for now")
                    .date(start)
                    .description("description for now")
                    .hearings(hearingOfaDay).build();
            calendar.add(calendarOfDay);

        }

        return calendar;
    }


    private HearingSearchCriteria getHearingSearchCriteriaFromJudgeSearch(JudgeSearchCriteria criteria) {

        LocalDate fromDate = null, toDate = null;

        if (criteria.getCurrentDate()) {
            toDate = fromDate = LocalDate.now();

        } else if (criteria.getCurrentWeek()) {
            LocalDate currentDate = LocalDate.now();

            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            // Calculate the start date of the current week (assuming Monday as the start of the week)
            fromDate = currentDate.minusDays(dayOfWeek.getValue() - 1);
            // Calculate the end date of the current week (assuming Sunday as the end of the week)
            toDate = fromDate.plusDays(6);
        } else if (criteria.getCurrentMonth()) {
            LocalDate currentDate = LocalDate.now();
            // Calculate the start date of the current month
            fromDate = currentDate.with(TemporalAdjusters.firstDayOfMonth());
            // Calculate the end date of the current month
            toDate = currentDate.with(TemporalAdjusters.lastDayOfMonth());
        } else if (criteria.getFromDate() != null && criteria.getToDate() != null) {
            fromDate = criteria.getFromDate();
            toDate = criteria.getToDate();
        }


        return HearingSearchCriteria.builder()
                .judgeId(criteria.getJudgeId())
                .tenantId(criteria.getTenantId())
                .fromDate(fromDate)
                .toDate(toDate).build();
    }


    // this ll updated default calendar of judge
    public List<JudgeCalendarRule> update(JudgeCalendarUpdateRequest judgeCalendarUpdateRequest) {

        //validate
        validator.validateUpdateJudgeCalendar(judgeCalendarUpdateRequest.getJudgeCalendarRule());

        //enrich
        enrichment.enrichUpdateJudgeCalendar(judgeCalendarUpdateRequest.getRequestInfo(), judgeCalendarUpdateRequest.getJudgeCalendarRule());

        //push to kafka
        producer.push(config.getUpdateJudgeCalendarTopic(), judgeCalendarUpdateRequest.getJudgeCalendarRule());

        return judgeCalendarUpdateRequest.getJudgeCalendarRule();

    }
}

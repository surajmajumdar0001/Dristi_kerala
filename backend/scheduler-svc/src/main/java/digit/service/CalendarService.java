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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Stream;

@Service
public class CalendarService {

    private final JudgeCalendarValidator validator;
    private final JudgeCalendarEnrichment enrichment;
    private final Producer producer;
    private final Configuration config;

    @Autowired
    private MdmsUtil mdmsUtil;

    @Autowired
    private ServiceConstants serviceConstants;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private HearingService hearingService;

    @Autowired
    private DefaultMasterDataHelper helper;


    @Autowired
    public CalendarService(JudgeCalendarValidator validator, JudgeCalendarEnrichment enrichment, Producer producer, Configuration config) {
        this.validator = validator;
        this.enrichment = enrichment;
        this.producer = producer;
        this.config = config;
    }


    public List<AvailabilityDTO> getJudgeAvailability(JudgeAvailabilitySearchRequest searchCriteriaRequest) {

        JudgeAvailabilitySearchCriteria criteria = searchCriteriaRequest.getCriteria();

        List<MdmsSlot> defaultSlots = helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME);

        double totalHrs = defaultSlots.stream().reduce(0.0, (total, slot) -> total + slot.getSlotDuration() / 60.0, Double::sum);

        //validating required fields
        validator.validateSearchRequest(criteria);
        //TODO:CONFIGURE
        if (criteria.getNumberOfSuggestedDays() == null) criteria.setNumberOfSuggestedDays(5);
        List<AvailabilityDTO> resultList = new ArrayList<>();
        HashMap<String, Double> dateMap = new HashMap<>();

        //TODO: need to configure
        // fetch mdms data of default calendar for court id
        Map<String, Map<String, JSONArray>> defaultCalendarResponse = mdmsUtil.fetchMdmsData(searchCriteriaRequest.getRequestInfo(), criteria.getTenantId(), serviceConstants.DEFAULT_JUDGE_CALENDAR_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_JUDGE_CALENDAR_MASTER_NAME));
        JSONArray court000334 = defaultCalendarResponse.get("schedule-hearing").get("COURT000334");

        //  fetch judge calendar rule for next thirty days
        List<JudgeCalendarRule> judgeCalendarRule = calendarRepository.getJudgeRule(criteria);

        int calendarLength = judgeCalendarRule.size();

        HearingSearchCriteria hearingSearchCriteria = HearingSearchCriteria.builder().fromDate(criteria.getFromDate())
                .judgeId(criteria.getJudgeId()).toDate(criteria.getFromDate().plusDays(30 * 6)).build();

        List<AvailabilityDTO> availableDateForHearing = hearingService.getAvailableDateForHearing(hearingSearchCriteria);
        int hearingLength = availableDateForHearing.size();

        //TODO: need to add mdms as well
        int loopLength = Math.max(Math.max(calendarLength, hearingLength), court000334.size());
        LocalDate lastDateInDefaultCalendar = null;
        for (int i = 0; i < loopLength; i++) {

            if (i < calendarLength) dateMap.put(judgeCalendarRule.get(i).getDate().toString(), -1.0);
            if (i < hearingLength) dateMap.put(availableDateForHearing.get(i).getDate(), availableDateForHearing.get(i).getOccupiedBandwidth());
            if (i < court000334.size()) {
                LinkedHashMap map = (LinkedHashMap) court000334.get(i);
                if (map.containsKey("date")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    String date = String.valueOf(map.get("date"));
                    dateMap.put(LocalDate.parse(date, formatter).toString(), -1.0);
                    lastDateInDefaultCalendar = LocalDate.parse(date, formatter);
                }

            }
            //TODO: need to add mdms as well

        }
        LocalDate dateAfterSixMonths = criteria.getFromDate().plusDays(30 * 6);   // configurable?
        LocalDate endDate = lastDateInDefaultCalendar.isBefore(dateAfterSixMonths) ? lastDateInDefaultCalendar.with(TemporalAdjusters.lastDayOfMonth()) : dateAfterSixMonths;
        // check startDate in date map if its exits and value is true then add to the result list
        Stream.iterate(criteria.getFromDate(), startDate -> startDate.isBefore(endDate), startDate -> startDate.plusDays(1))
                .takeWhile(startDate -> resultList.size() != criteria.getNumberOfSuggestedDays()).forEach(startDate -> {

                    if (dateMap.containsKey(startDate.toString()) && dateMap.get(startDate.toString()) != -1.0 && dateMap.get(startDate.toString()) < totalHrs)
                        resultList.add(AvailabilityDTO.builder()
                                .date(startDate.toString())
                                .occupiedBandwidth(dateMap.get(startDate.toString())).build());

                    // this case will cover no holiday,no leave and no hearing for day
                    if (!dateMap.containsKey(startDate.toString()))
                        resultList.add(AvailabilityDTO.builder()
                                .date(startDate.toString())
                                .occupiedBandwidth(0.0).build());


                });
        //TODO: throw error if there is no date in next six month after from date

        return resultList;


    }


    public List<HearingCalendar> getJudgeCalendar(JudgeCalendarSearchRequest searchCriteriaRequest) {

        CalendarSearchCriteria criteria = searchCriteriaRequest.getCriteria();
        validator.validateSearchRequest(criteria);

        List<HearingCalendar> calendar = new ArrayList<>();
        HashMap<LocalDate, List<ScheduleHearing>> dayHearingMap = new HashMap<>();
        HashMap<LocalDate, Object> leaveMap = new HashMap<>();


        //TODO: need to configure
        //fetch mdms data of default calendar for court id and judge id
        Map<String, Map<String, JSONArray>> defaultCourtCalendar = mdmsUtil.fetchMdmsData(searchCriteriaRequest.getRequestInfo(), criteria.getTenantId(), serviceConstants.DEFAULT_JUDGE_CALENDAR_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_JUDGE_CALENDAR_MASTER_NAME));
        JSONArray court000334 = defaultCourtCalendar.get("schedule-hearing").get("COURT000334");


        //fetch judge calendar rule
        List<JudgeCalendarRule> judgeCalendarRule = calendarRepository.getJudgeRule(criteria);

        int loopLength = Math.max(judgeCalendarRule.size(), court000334.size());
        for (int i = 0; i < loopLength; i++) {

            if (i < judgeCalendarRule.size())
                leaveMap.put(judgeCalendarRule.get(i).getDate(), judgeCalendarRule.get(i));
            if (i < court000334.size()) {
                LinkedHashMap map = (LinkedHashMap) court000334.get(i);
                if (map.containsKey("date")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    String date = String.valueOf(map.get("date"));
                    leaveMap.put(LocalDate.parse(date, formatter), map);
                }

            }

        }

        HearingSearchCriteria hearingSearchCriteria = getHearingSearchCriteriaFromJudgeSearch(criteria);
        // sort on the basis of start time
        List<ScheduleHearing> hearings = hearingService.search(HearingSearchRequest.builder().criteria(hearingSearchCriteria).build());

        hearings.forEach((hearing) -> {

            if (dayHearingMap.containsKey(hearing.getDate())) {
                dayHearingMap.get(hearing.getDate()).add(hearing);
            } else {
                dayHearingMap.put(hearing.getDate(), new ArrayList<>(Collections.singletonList(hearing)));
            }

        });

        //generating calendar response
        for (LocalDate start = hearingSearchCriteria.getFromDate(); start.isBefore(hearingSearchCriteria.getToDate()) || start.isEqual(hearingSearchCriteria.getToDate()); start = start.plusDays(1)) {
            List<ScheduleHearing> hearingOfaDay = dayHearingMap.getOrDefault(start, new ArrayList<>());

            HearingCalendar calendarOfDay = HearingCalendar.builder()
                    .judgeId(criteria.getJudgeId())
                    .isOnLeave(leaveMap.containsKey(start) && leaveMap.get(start) instanceof JudgeCalendarRule)
                    .isHoliday(leaveMap.containsKey(start) && leaveMap.get(start) instanceof LinkedHashMap<?, ?>)
                    .notes("notes for now")
                    .date(start)
                    .description("description for now")
                    .hearings(hearingOfaDay).build();
            calendar.add(calendarOfDay);

        }

        return calendar;
    }

    //TODO: CHANGE TO UPSERT( change in persister file only )
    public List<JudgeCalendarRule> upsert(JudgeCalendarUpdateRequest judgeCalendarUpdateRequest) {

        //validate
        validator.validateUpdateJudgeCalendar(judgeCalendarUpdateRequest.getJudgeCalendarRule());

        //enrich
        enrichment.enrichUpdateJudgeCalendar(judgeCalendarUpdateRequest.getRequestInfo(), judgeCalendarUpdateRequest.getJudgeCalendarRule());

        //push to kafka
        producer.push(config.getUpdateJudgeCalendarTopic(), judgeCalendarUpdateRequest.getJudgeCalendarRule());

        return judgeCalendarUpdateRequest.getJudgeCalendarRule();

    }


    //TODO:create helper class and move this into the class
    private HearingSearchCriteria getHearingSearchCriteriaFromJudgeSearch(CalendarSearchCriteria criteria) {

        LocalDate fromDate = null, toDate = null;

        if (criteria.getPeriodType() != null) {
            Pair<LocalDate, LocalDate> pair = getFromAndToDateFromPeriodType(criteria.getPeriodType());
            fromDate = pair.getKey();
            toDate = pair.getValue();
        }
        //this ll override the switch case result (providing the highest priority to custom range)
        if (criteria.getFromDate() != null && criteria.getToDate() != null) {
            fromDate = criteria.getFromDate();
            toDate = criteria.getToDate();
        }

        return HearingSearchCriteria.builder()
                .judgeId(criteria.getJudgeId())
                .tenantId(criteria.getTenantId())
                .fromDate(fromDate)
                .toDate(toDate)
                .status(Collections.singletonList(Status.SCHEDULED)).build();

    }


    public Pair<LocalDate, LocalDate> getFromAndToDateFromPeriodType(PeriodType periodType) {

        Pair<LocalDate, LocalDate> pair = new Pair<>();

        LocalDate fromDate = null, toDate = null;
        LocalDate currentDate = LocalDate.now();

        switch (periodType) {

            case CURRENT_DATE -> toDate = fromDate = currentDate;
            case CURRENT_WEEK -> {
                DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                // Calculate the start date of the current week (assuming Monday as the start of the week)
                fromDate = currentDate.minusDays(dayOfWeek.getValue() - 1);
                // Calculate the end date of the current week (assuming Sunday as the end of the week)
                toDate = fromDate.plusDays(6);
            }
            case CURRENT_MONTH -> {
                // Calculate the start date of the current month
                fromDate = currentDate.with(TemporalAdjusters.firstDayOfMonth());
                // Calculate the end date of the current month
                toDate = currentDate.with(TemporalAdjusters.lastDayOfMonth());
            }
            case CURRENT_YEAR -> {
                // Calculate the start date of the current year
                fromDate = currentDate.with(TemporalAdjusters.firstDayOfYear());
                // Calculate the end date of the current year
                toDate = currentDate.with(TemporalAdjusters.lastDayOfYear());

            }
        }

        pair.setKey(fromDate);
        pair.setValue(toDate);
        return pair;

    }
}

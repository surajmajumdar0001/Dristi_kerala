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
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Stream;

/**
 * Contains method to retrieve judge availability, judge calendar ,update judge rule by judge id
 */
@Service
@Slf4j
public class CalendarService {

    private final JudgeCalendarValidator validator;

    private final JudgeCalendarEnrichment enrichment;

    private final Producer producer;

    private final Configuration config;

    private final MdmsUtil mdmsUtil;

    private final ServiceConstants serviceConstants;

    private final CalendarRepository calendarRepository;

    private final HearingService hearingService;

    private final DefaultMasterDataHelper helper;


    @Autowired
    public CalendarService(JudgeCalendarValidator validator, JudgeCalendarEnrichment enrichment, Producer producer, Configuration config, MdmsUtil mdmsUtil, ServiceConstants serviceConstants, CalendarRepository calendarRepository, HearingService hearingService, DefaultMasterDataHelper helper) {
        this.validator = validator;
        this.enrichment = enrichment;
        this.producer = producer;
        this.config = config;
        this.mdmsUtil = mdmsUtil;
        this.serviceConstants = serviceConstants;
        this.calendarRepository = calendarRepository;
        this.hearingService = hearingService;
        this.helper = helper;
    }

    /**
     * This function calculate availability of judge by considering his leaves , hearings and default court calendar
     * @param searchCriteriaRequest not null request which contains request info and judge availability search criteria
     * @return list of availability dto
     * @exception CustomException if there are no available date from start date (fromDate) in next six months
     */

    public List<AvailabilityDTO> getJudgeAvailability(JudgeAvailabilitySearchRequest searchCriteriaRequest) {
        JudgeAvailabilitySearchCriteria criteria = searchCriteriaRequest.getCriteria();
        log.info("operation = getJudgeAvailability, result = IN_PROGRESS, judgeId = {},tenantId ={}, courtId = {}", criteria.getJudgeId(), criteria.getTenantId(), criteria.getCourtId());

        // validating required fields
        validator.validateSearchRequest(criteria);

        List<AvailabilityDTO> resultList = new ArrayList<>();
        HashMap<String, Double> dateMap = new HashMap<>();

        // retrieve type of hearings from master data
        List<MdmsSlot> defaultSlots = helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME);

        // calculate bandwidth for judge from slot of court
        double totalHrs = defaultSlots.stream().reduce(0.0, (total, slot) -> total + slot.getSlotDuration() / 60.0, Double::sum);

//      if (criteria.getNumberOfSuggestedDays() == null) criteria.setNumberOfSuggestedDays(5);

        //TODO:Configure for different courts
        Map<String, Map<String, JSONArray>> defaultCalendarResponse = mdmsUtil.fetchMdmsData(searchCriteriaRequest.getRequestInfo(), criteria.getTenantId(), serviceConstants.DEFAULT_JUDGE_CALENDAR_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_JUDGE_CALENDAR_MASTER_NAME));
        JSONArray court000334 = defaultCalendarResponse.get("schedule-hearing").get("COURT000334");

        //  fetch judge calendar rule for next thirty days
        List<JudgeCalendarRule> judgeCalendarRule = calendarRepository.getJudgeRule(criteria);

        int calendarLength = judgeCalendarRule.size();

        // fetch available dates of  judge for next 6 month
        HearingSearchCriteria hearingSearchCriteria = HearingSearchCriteria.builder().fromDate(criteria.getFromDate())
                .judgeId(criteria.getJudgeId()).toDate(criteria.getFromDate().plusDays(30 * 6)).build();

        List<AvailabilityDTO> availableDateForHearing;

        try {
            availableDateForHearing = hearingService.getAvailableDateForHearing(hearingSearchCriteria);
        } catch (Exception e) {
            log.error("error occurred while retrieving available date for judge from hearings, searchCriteria= {} ", hearingSearchCriteria);
            throw new CustomException("EXTERNAL_SERVICE_CALL_EXCEPTION", "Failed to fetch available dates");
        }
        int hearingLength = availableDateForHearing.size();

        int loopLength = Math.max(Math.max(calendarLength, hearingLength), court000334.size());
        LocalDate lastDateInDefaultCalendar = null;
        for (int i = 0; i < loopLength; i++) {

            if (i < hearingLength)
                dateMap.put(availableDateForHearing.get(i).getDate(), availableDateForHearing.get(i).getOccupiedBandwidth());
            if (i < court000334.size()) {
                LinkedHashMap map = (LinkedHashMap) court000334.get(i);
                if (map.containsKey("date")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    String date = String.valueOf(map.get("date"));
                    dateMap.put(LocalDate.parse(date, formatter).toString(), -1.0);
                    lastDateInDefaultCalendar = LocalDate.parse(date, formatter);
                }

            }
            if (i < calendarLength) dateMap.put(judgeCalendarRule.get(i).getDate().toString(), -1.0);
        }

        // calculating date after 6 month from provided date
        LocalDate dateAfterSixMonths = criteria.getFromDate().plusDays(30 * 6);// configurable?

        //last date which is store in default calendar
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

        if (resultList.isEmpty()) {
            throw new CustomException("NO_AVAILABLE_DATES", "There are no available dates in next 6 months from provided start date");
        }
        log.info("operation = getJudgeAvailability, result = SUCCESS, Availability = {}", resultList);

        return resultList;


    }

    /**
     * This function calculate the judge calendar for asked period time considering judge personal rules , default court calendar and judge hearings
     * @param searchCriteriaRequest  not null request which contains request info and calendar search criteria
     * @return  list of HearingCalendar
     */

    public List<HearingCalendar> getJudgeCalendar(JudgeCalendarSearchRequest searchCriteriaRequest) {

        CalendarSearchCriteria criteria = searchCriteriaRequest.getCriteria();
        log.info("operation = getJudgeCalendar, result = IN_PROGRESS, tenantId= {}, judgeId = {}, courtId = {}", criteria.getTenantId(), criteria.getJudgeId(), criteria.getCourtId());

        validator.validateSearchRequest(criteria);

        List<HearingCalendar> calendar = new ArrayList<>();
        HashMap<LocalDate, List<ScheduleHearing>> dayHearingMap = new HashMap<>();
        HashMap<LocalDate, Object> leaveMap = new HashMap<>();


        //TODO: need to configure
        //fetch mdms data of default calendar for court id and judge id
        Map<String, Map<String, JSONArray>> defaultCourtCalendar = mdmsUtil.fetchMdmsData(searchCriteriaRequest.getRequestInfo(), criteria.getTenantId(), serviceConstants.DEFAULT_JUDGE_CALENDAR_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_JUDGE_CALENDAR_MASTER_NAME));
        JSONArray court000334 = defaultCourtCalendar.get("schedule-hearing").get("COURT000334");


        // getting from date and to date and assigning it to criteria
        if (criteria.getPeriodType() != null) {
            Pair<LocalDate, LocalDate> fromDateToDate = getFromAndToDateFromPeriodType(criteria.getPeriodType());
            criteria.setFromDate(fromDateToDate.getKey());
            criteria.setToDate(fromDateToDate.getValue());
        }
        //fetch judge calendar rule
        List<JudgeCalendarRule> judgeCalendarRule;
        try {
            judgeCalendarRule = calendarRepository.getJudgeRule(criteria);
        } catch (Exception e) {
            log.error("error occurred while retrieving judge rules from judge calendar rule table");
            throw new CustomException("DK_SH_APP_ERR", "error occurred while fetching judge rules");
        }

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
        List<ScheduleHearing> hearings;
        try {
            hearings = hearingService.search(HearingSearchRequest.builder().criteria(hearingSearchCriteria).build(), null, null);
        } catch (Exception e) {
            log.error("");
            throw new CustomException("", "");
        }

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
                    .notes("note")
                    .date(start)
                    .description("description")
                    .hearings(hearingOfaDay).build();
            calendar.add(calendarOfDay);

        }
        log.info("operation = getJudgeAvailability, result = SUCCESS, HearingCalendar = {}", calendar);

        return calendar;
    }

    /**
     * This function update the judge calendar rule for judge
     * @param judgeCalendarUpdateRequest not null request with request info and list of JudgeCalendarRule
     * @return list of judge calendar rule
     */
    public List<JudgeCalendarRule> upsert(JudgeCalendarUpdateRequest judgeCalendarUpdateRequest) {

        List<JudgeCalendarRule> judgeCalendarRule = judgeCalendarUpdateRequest.getJudgeCalendarRule();
        log.info("operation = upsert, result = IN_PROGRESS, size={}", judgeCalendarRule.size());
        //validate
        validator.validateUpdateJudgeCalendar(judgeCalendarRule);
        //enrich
        enrichment.enrichUpdateJudgeCalendar(judgeCalendarUpdateRequest.getRequestInfo(), judgeCalendarRule);
        //push to kafka
        producer.push(config.getUpdateJudgeCalendarTopic(), judgeCalendarRule);
        log.info("operation = upsert, result = SUCCESS, size={}", judgeCalendarRule.size());

        return judgeCalendarRule;

    }

    /**
     * Function to convert judge search criteria to hearing search criteria
     * @param criteria calendar search criteria
     * @return Hearing search criteria
     */

    private HearingSearchCriteria getHearingSearchCriteriaFromJudgeSearch(CalendarSearchCriteria criteria) {
        log.info("operation = getHearingSearchCriteriaFromJudgeSearch, result = IN_PROGRESS, CalendarSearchCriteria = {}", criteria);

        LocalDate fromDate = null, toDate = null;

        if (criteria.getFromDate() != null && criteria.getToDate() != null) {
            fromDate = criteria.getFromDate();
            toDate = criteria.getToDate();
        }

        log.info("operation = getHearingSearchCriteriaFromJudgeSearch, result = SUCCESS, HearingSearchCriteria = {}", criteria);

        return HearingSearchCriteria.builder()
                .judgeId(criteria.getJudgeId())
                .tenantId(criteria.getTenantId())
                .fromDate(fromDate)
                .toDate(toDate)
                .tenantId(criteria.getTenantId())
                .status(Collections.singletonList(Status.SCHEDULED)).build();

    }

    /**
     * Function to process period type enum and convert it into form and to date
     * @param periodType enum
     * @return Pair Object with from date in key and to date in value
     */
    public Pair<LocalDate, LocalDate> getFromAndToDateFromPeriodType(PeriodType periodType) {
        log.info("operation = getFromAndToDateFromPeriodType, result = IN_PROGRESS, PeriodType = {}", periodType);
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

        log.info("operation = getFromAndToDateFromPeriodType, result = SUCCESS, fromDate = {} , toDate = {}", fromDate, toDate);
        return pair;

    }
}

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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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


    public List<String> getJudgeAvailability(JudgeAvailabilitySearchRequest searchCriteriaRequest) {

        List<String> resultList = new ArrayList<>();
        HashMap<String, Boolean> dateMap = new HashMap<>();

        int calendarLength;
        int hearingLength;
        JudgeSearchCriteria criteria = searchCriteriaRequest.getCriteria();

        //  fetch mdms data of default calendar for court id
        Map<String, Map<String, JSONArray>> defaultCalendarResponse = mdmsUtil.fetchMdmsData(searchCriteriaRequest.getRequestInfo(), criteria.getTenantId(), serviceConstants.DEFAULT_JUDGE_CALENDAR_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_JUDGE_CALENDAR_MASTER_NAME));

        //  fetch judge calendar rule for next thirty days
        List<JudgeCalendar> judgeCalendar = calendarRepository.getJudgeRule(criteria);


        calendarLength = judgeCalendar.size();


        HearingSearchCriteria hearingSearchCriteria = HearingSearchCriteria.builder().fromDate(criteria.getFromDate()).judgeId(criteria.getJudgeId()).toDate(criteria.getFromDate().plusDays(30)).build();

        List<String> availableDateForHearing = hearingService.getAvailableDateForHearing(hearingSearchCriteria);
        hearingLength = availableDateForHearing.size();


        int loopLength = Math.max(calendarLength, hearingLength);  // need to add mdms as well

        for (int i = 0; i < loopLength; i++) {

            if (i < calendarLength) dateMap.put(judgeCalendar.get(i).getDate().toString(), false);
            if (i < hearingLength) dateMap.put(availableDateForHearing.get(i), true);

            //one more if condition for mdms
        }

        LocalDateTime endDate = criteria.getFromDate().plusDays(30);
        // check startDate in date map if its exits and value is true then add to the result list
        Stream.iterate(criteria.getFromDate(), startDate -> startDate.isBefore(endDate), startDate -> startDate.plusDays(1)).forEach(startDate -> {
            if (dateMap.containsKey(startDate.toString()) && dateMap.get(startDate.toString()))
                resultList.add(startDate.toString());
            else resultList.add(startDate.toString());
        });

        return resultList;


    }


    // this method ll provide the judge calendar for a current month (default)

    public List<JudgeCalendar> getJudgeCalendar(JudgeAvailabilitySearchRequest searchCriteriaRequest) {
        JudgeSearchCriteria criteria = searchCriteriaRequest.getCriteria();


        //calendar module name and master name list should be provided in search criteria(should be judge id or court id )

        //fetch mdms data of default calendar for court id and judge id
        mdmsUtil.fetchMdmsData(searchCriteriaRequest.getRequestInfo(), criteria.getTenantId(), serviceConstants.DEFAULT_JUDGE_CALENDAR_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_JUDGE_CALENDAR_MASTER_NAME));


        //fetch judge calendar rule

        //fetch judge calendar availability for number of days

        //merge all three and return the calendar

        //if slotting is there then apply slotting

        return null;
    }


    // this ll updated default calendar of judge
    public List<JudgeCalendar> update(JudgeCalendarUpdateRequest judgeCalendarUpdateRequest) {

        //validate
        validator.validateUpdateJudgeCalendar(judgeCalendarUpdateRequest.getJudgeCalendar());

        //enrich
        enrichment.enrichUpdateJudgeCalendar(judgeCalendarUpdateRequest.getRequestInfo(), judgeCalendarUpdateRequest.getJudgeCalendar());

        //push to kafka
        producer.push(config.getUpdateJudgeCalendarTopic(), judgeCalendarUpdateRequest.getJudgeCalendar());

        return judgeCalendarUpdateRequest.getJudgeCalendar();

    }
}

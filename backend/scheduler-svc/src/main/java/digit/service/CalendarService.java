package digit.service;


import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.enrichment.JudgeCalendarEnrichment;
import digit.kafka.Producer;
import digit.util.MdmsUtil;
import digit.validator.JudgeCalendarValidator;
import digit.web.models.JudgeAvailabilitySearchRequest;
import digit.web.models.JudgeCalendar;
import digit.web.models.JudgeCalendarUpdateRequest;
import digit.web.models.JudgeSearchCriteria;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CalendarService {

    private final JudgeCalendarValidator validator;
    private final JudgeCalendarEnrichment enrichment;

    private final Producer producer;

    private Configuration config;

    @Autowired
    private MdmsUtil mdmsUtil;

    private ServiceConstants serviceConstants;


    @Autowired
    public CalendarService(JudgeCalendarValidator validator, JudgeCalendarEnrichment enrichment, Producer producer, Configuration config) {
        this.validator = validator;
        this.enrichment = enrichment;
        this.producer = producer;
        this.config = config;
    }


    public void getJudgeAvailability(JudgeAvailabilitySearchRequest searchCriteriaRequest) {
        List<JudgeCalendar> resultList = new ArrayList<>();

        JudgeSearchCriteria criteria = searchCriteriaRequest.getCriteria();


        // m
        mdmsUtil.fetchMdmsData(searchCriteriaRequest.getRequestInfo(),criteria.getTenantId(),serviceConstants.DEFAULT_JUDGE_CALENDAR_MODULE_NAME, Collections.singletonList(serviceConstants.DEFAULT_JUDGE_CALENDAR_MASTER_NAME));

        //fetch mdms data of default calendar for court id and judge id

        //fetch judge calendar rule

        // fetch all the hearing of judge from startdate onwards

        //iterate over above result check where availability is there and add to the
        // resultList till the ask number of date is completed


    }


    // this method ll provide the the judge calendar for a current month (default)

    public List<JudgeCalendar> getJudgeCalendar(JudgeAvailabilitySearchRequest searchCriteria) {

        //fetch mdms data of default calendar for court id and judge id

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

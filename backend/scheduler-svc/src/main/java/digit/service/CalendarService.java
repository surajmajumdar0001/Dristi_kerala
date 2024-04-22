package digit.service;


import digit.web.models.JudgeCalendar;
import digit.web.models.JudgeSearchCriteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {


    public void getJudgeAvailability(JudgeSearchCriteria searchCriteria) {
        List<JudgeCalendar> resultList = new ArrayList<>();

        //fetch mdms data of default calendar for court id and judge id
        //fetch judge calendar rule

        // fetch all the hearing of judge from startdate onwards

        //iterate over above result check where availability is there and add to the
        // resultList till the ask number of date is completed


    }

    public List<JudgeCalendar> getJudgeCalendar(JudgeSearchCriteria searchCriteria) {


        //fetch mdms data of default calendar for court id and judge id
        //fetch judge calendar rule

        //fetch judge calendar availability for number of days

        // merge all three and return the calendar

        //if slotting is there then apply slotting
        return null;
    }


    public void update(List<JudgeCalendar> judgeCalendar) {


        //simply validate and update the judge calendar rule table for particular judge calendar

    }
}

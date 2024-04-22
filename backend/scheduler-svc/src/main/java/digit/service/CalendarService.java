package digit.service;


import digit.web.models.JudgeCalendar;
import digit.web.models.JudgeSearchCriteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {

    // this method will retrieve the availability of judge for n next working from tha pass start date
    public void getJudgeAvailability(JudgeSearchCriteria searchCriteria) {
        List<JudgeCalendar> resultList = new ArrayList<>();

        //fetch mdms data of default calendar for court id and judge id
        //fetch judge calendar rule

        // fetch all the hearing of judge from startdate onwards

        //iterate over above result check where availability is there and add to the
        // resultList till the ask number of date is completed


    }


    // this method ll provide the the judge calendar for a current month (default)

    public List<JudgeCalendar> getJudgeCalendar(JudgeSearchCriteria searchCriteria) {

        //fetch mdms data of default calendar for court id and judge id

        //fetch judge calendar rule

        //fetch judge calendar availability for number of days

        //merge all three and return the calendar

        //if slotting is there then apply slotting

        return null;
    }


    // this ll updated default calendar of judge
    public void update(List<JudgeCalendar> judgeCalendar) {

        //validate

        //enhance

        //push to kafka

    }
}

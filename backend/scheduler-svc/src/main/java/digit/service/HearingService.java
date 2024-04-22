package digit.service;


import digit.web.models.ScheduleHearing;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HearingService {



    public void scheduleHearing (List<ScheduleHearing> schedulingRequests){

        //validate hearing request here


        // enhance the hearing request here

        //1.0 id gen 2.0 audit details


        //push to kafka
    }

    // to update the status of existing hearing to reschedule
    private void updateScheduledHearing(List<ScheduleHearing> updateScheduleHearingRequest){
        // validate request

        //enhance the request
        // updateStatus and audit details

        //push to kafka
    }
}

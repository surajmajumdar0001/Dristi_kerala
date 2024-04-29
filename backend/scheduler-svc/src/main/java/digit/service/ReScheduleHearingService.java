package digit.service;


import digit.web.models.ReScheduleHearing;
import digit.web.models.ReScheduleHearingReqSearchRequest;
import digit.web.models.ReScheduleHearingRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReScheduleHearingService {

    public List<ReScheduleHearing> create(ReScheduleHearingRequest reScheduleHearingsRequest) {

        //TODO: received rescheduling request and stored in rescheduling table (with enrichment)
        return reScheduleHearingsRequest.getReScheduleHearing();

    }

    // workflow will be there so only one update at a time
    public ReScheduleHearing update(ReScheduleHearingRequest reScheduleHearingsRequest) {

        //TODO: received update rescheduling request and stored in rescheduling table (with enrichment)
        return reScheduleHearingsRequest.getReScheduleHearing().get(0);

    }

    public List<ReScheduleHearing> search(ReScheduleHearingReqSearchRequest request) {
        return null;
    }
}

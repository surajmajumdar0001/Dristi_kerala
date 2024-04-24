package digit.service;


import digit.config.Configuration;
import digit.enrichment.HearingEnrichment;
import digit.kafka.Producer;
import digit.repository.HearingRepository;
import digit.validator.HearingValidator;
import digit.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HearingService {


    private final HearingValidator hearingValidator;

    private final HearingEnrichment hearingEnrichment;

    private final Producer producer;

    private final Configuration config;
    @Autowired
    private HearingRepository hearingRepository;


    @Autowired
    public HearingService(HearingValidator hearingValidator, HearingEnrichment hearingEnrichment, Producer producer, Configuration config) {
        this.hearingValidator = hearingValidator;
        this.hearingEnrichment = hearingEnrichment;
        this.producer = producer;
        this.config = config;
    }


    public List<ScheduleHearing> scheduleHearing(ScheduleHearingRequest schedulingRequests) {

        //validate hearing request here
        hearingValidator.validateHearing(schedulingRequests.getHearing());

        // enhance the hearing request here
        hearingEnrichment.enrichScheduleHearing(schedulingRequests.getRequestInfo(), schedulingRequests.getHearing());

        //push to kafka
        producer.push(config.getScheduleHearingTopic(), schedulingRequests.getHearing());

        return schedulingRequests.getHearing();
    }

    // to update the status of existing hearing to reschedule
    private void updateScheduledHearing(List<ScheduleHearing> updateScheduleHearingRequest) {
        //  validate request

        //  enrich the request
        //  updateStatus and audit details

        //  push to kafka
    }


    public List<ReScheduleHearing> reScheduleHearingRequest(List<ReScheduleHearing> reScheduleHearingsRequest) {


        return reScheduleHearingsRequest;

    }


    public List<ScheduleHearing> getJudgeHearing(HearingSearchCriteria searchCriteria) {

        List<ScheduleHearing> judgeHearings = hearingRepository.getJudgeHearing(searchCriteria);
        return null;
    }

    public List<String> getAvailableDateForHearing(HearingSearchCriteria hearingSearchCriteria) {


        List<String> availableDates = hearingRepository.getAvailableDatesOfJudges(hearingSearchCriteria);
        return availableDates;
    }

    public List<ReScheduleHearing> search(HearingSearchRequest request) {
        return null;
    }
}

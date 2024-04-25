package digit.service;


import digit.config.Configuration;
import digit.enrichment.HearingEnrichment;
import digit.kafka.Producer;
import digit.repository.HearingRepository;
import digit.validator.HearingValidator;
import digit.web.models.*;
import jakarta.validation.Valid;
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


    public List<ScheduleHearing> schedule(ScheduleHearingRequest schedulingRequests) {

        //validate hearing request here
        hearingValidator.validateHearing(schedulingRequests.getHearing());

        // enhance the hearing request here
        hearingEnrichment.enrichScheduleHearing(schedulingRequests.getRequestInfo(), schedulingRequests.getHearing());

        //push to kafka
        producer.push(config.getScheduleHearingTopic(), schedulingRequests.getHearing());

        return schedulingRequests.getHearing();
    }

    // to update the status of existing hearing to reschedule
    public  List<ScheduleHearing> update(ScheduleHearingRequest scheduleHearingRequest) {

        hearingValidator.validateHearingOnUpdate(scheduleHearingRequest);

        hearingEnrichment.enrichUpdateScheduleHearing(scheduleHearingRequest.getRequestInfo(), scheduleHearingRequest.getHearing());

        producer.push(config.getScheduleHearingUpdateTopic(), scheduleHearingRequest.getHearing());

        return scheduleHearingRequest.getHearing();

    }

    public List<ScheduleHearing> search(HearingSearchRequest request) {

        return hearingRepository.getHearings(request.getCriteria());

    }


    public List<AvailabilityDTO> getAvailableDateForHearing(HearingSearchCriteria hearingSearchCriteria) {

        return hearingRepository.getAvailableDatesOfJudges(hearingSearchCriteria);
    }


}
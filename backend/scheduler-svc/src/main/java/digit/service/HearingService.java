package digit.service;


import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.enrichment.HearingEnrichment;
import digit.helper.DefaultMasterDataHelper;
import digit.kafka.Producer;
import digit.repository.HearingRepository;
import digit.validator.HearingValidator;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HearingService {


    private final HearingValidator hearingValidator;

    private final HearingEnrichment hearingEnrichment;

    private final Producer producer;

    private final Configuration config;
    @Autowired
    private HearingRepository hearingRepository;

    @Autowired
    private ServiceConstants serviceConstants;

    @Autowired
    private DefaultMasterDataHelper helper;





    @Autowired
    public HearingService(HearingValidator hearingValidator, HearingEnrichment hearingEnrichment, Producer producer, Configuration config) {
        this.hearingValidator = hearingValidator;
        this.hearingEnrichment = hearingEnrichment;
        this.producer = producer;
        this.config = config;
    }


    public List<ScheduleHearing> schedule(ScheduleHearingRequest schedulingRequests) {


        List<MdmsSlot> defaultSlots = helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME);

        double totalHrs = defaultSlots.stream().reduce(0.0, (total, slot) -> total + slot.getSlotDuration() / 60.0, Double::sum);
        // get hearings and default timing
        List<MdmsHearing> defaultHearings = helper.getDataFromMDMS(MdmsHearing.class, serviceConstants.DEFAULT_HEARING_MASTER_NAME);
        Map<String, MdmsHearing> hearingTypeMap = defaultHearings.stream().collect(Collectors.toMap(
                MdmsHearing::getHearingType,
                obj -> obj
        ));
        //validate hearing request here
        hearingValidator.validateHearing(schedulingRequests, totalHrs, hearingTypeMap);

        // enhance the hearing request here
        hearingEnrichment.enrichScheduleHearing(schedulingRequests,defaultSlots,hearingTypeMap);

        //push to kafka
        producer.push(config.getScheduleHearingTopic(), schedulingRequests.getHearing());

        return schedulingRequests.getHearing();
    }

    // to update the status of existing hearing to reschedule
    public List<ScheduleHearing> update(ScheduleHearingRequest scheduleHearingRequest) {

        hearingValidator.validateHearingOnUpdate(scheduleHearingRequest);

        hearingEnrichment.enrichUpdateScheduleHearing(scheduleHearingRequest.getRequestInfo(), scheduleHearingRequest.getHearing());

        producer.push(config.getScheduleHearingUpdateTopic(), scheduleHearingRequest.getHearing());

        return scheduleHearingRequest.getHearing();

    }

    public List<ScheduleHearing> search(HearingSearchRequest request, Integer limit, Integer offset) {

        return hearingRepository.getHearings(request.getCriteria(), limit, offset);

    }


    public List<AvailabilityDTO> getAvailableDateForHearing(HearingSearchCriteria hearingSearchCriteria) {

        return hearingRepository.getAvailableDatesOfJudges(hearingSearchCriteria);
    }


    public void updateBulk(ScheduleHearingRequest request, List<MdmsSlot> defaultSlot, Map<String, MdmsHearing> hearingTypeMap) {


        hearingEnrichment.enrichBulkReschedule(request,defaultSlot,hearingTypeMap);

        producer.push(config.getScheduleHearingUpdateTopic(), request.getHearing());
    }
}

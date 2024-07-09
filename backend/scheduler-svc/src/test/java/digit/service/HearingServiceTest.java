package digit.service;

import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.enrichment.HearingEnrichment;
import digit.helper.DefaultMasterDataHelper;
import digit.kafka.Producer;
import digit.validator.HearingValidator;
import digit.web.models.MdmsHearing;
import digit.web.models.MdmsSlot;
import digit.web.models.ScheduleHearing;
import digit.web.models.ScheduleHearingRequest;
import org.egov.common.contract.request.RequestInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HearingServiceTest {

    @Mock
    private Configuration config;

    @Mock
    private ServiceConstants serviceConstants;

    @Mock
    private Producer producer;

    @Mock
    private HearingEnrichment enrichment;

    @Mock
    private DefaultMasterDataHelper helper;

    @Mock
    private HearingValidator validator;

    @InjectMocks
    private HearingService hearingService;

    @Test
    void testSchedule() {
        ScheduleHearingRequest schedulingRequests = new ScheduleHearingRequest();
        ScheduleHearing hearing = new ScheduleHearing();
        schedulingRequests.setHearing(Arrays.asList(hearing));

        MdmsSlot slot = new MdmsSlot();
        slot.setSlotDuration(120); // in minutes
        List<MdmsSlot> defaultSlots = Arrays.asList(slot);

        MdmsHearing hearingData = new MdmsHearing();
        hearingData.setHearingType("default");
        List<MdmsHearing> defaultHearings = Arrays.asList(hearingData);
        Map<String, MdmsHearing> hearingTypeMap = defaultHearings.stream().collect(Collectors.toMap(
                MdmsHearing::getHearingType,
                obj -> obj
        ));

        when(helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME)).thenReturn(defaultSlots);
        when(helper.getDataFromMDMS(MdmsHearing.class, serviceConstants.DEFAULT_HEARING_MASTER_NAME)).thenReturn(defaultHearings);
        when(config.getScheduleHearingTopic()).thenReturn("scheduleHearingTopic");

        List<ScheduleHearing> hearingList = hearingService.schedule(schedulingRequests);

        double totalHrs = defaultSlots.stream().reduce(0.0, (total, slotData) -> total + slotData.getSlotDuration() / 60.0, Double::sum);

        verify(validator, times(1)).validateHearing(schedulingRequests, totalHrs, hearingTypeMap);
        verify(enrichment, times(1)).enrichScheduleHearing(schedulingRequests, defaultSlots, hearingTypeMap);
        verify(producer, times(1)).push("scheduleHearingTopic", schedulingRequests.getHearing());

        assertEquals(schedulingRequests.getHearing(), hearingList);
    }

    @Test
    void testUpdate() {
        ScheduleHearingRequest scheduleHearingRequest = new ScheduleHearingRequest();
        RequestInfo requestInfo = new RequestInfo();
        ScheduleHearing hearing = new ScheduleHearing();
        scheduleHearingRequest.setRequestInfo(requestInfo);
        scheduleHearingRequest.setHearing(Arrays.asList(hearing));

        when(config.getScheduleHearingUpdateTopic()).thenReturn("scheduleHearingUpdateTopic");

        List<ScheduleHearing> result = hearingService.update(scheduleHearingRequest);

        verify(enrichment, times(1)).enrichUpdateScheduleHearing(requestInfo, scheduleHearingRequest.getHearing());
        verify(producer, times(1)).push(eq("scheduleHearingUpdateTopic"), any());

        assertEquals(1, result.size());
        assertEquals(hearing, result.get(0));
    }
}

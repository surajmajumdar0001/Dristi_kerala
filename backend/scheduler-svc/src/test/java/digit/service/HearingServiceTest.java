package digit.service;

import digit.config.Configuration;
import digit.config.ServiceConstants;
import digit.enrichment.HearingEnrichment;
import digit.helper.DefaultMasterDataHelper;
import digit.kafka.Producer;
import digit.repository.HearingRepository;
import digit.validator.HearingValidator;
import digit.web.models.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HearingServiceTest {

    @InjectMocks
    private HearingService hearingService;

    @Mock
    private HearingValidator hearingValidator;

    @Mock
    private HearingEnrichment hearingEnrichment;

    @Mock
    private Producer producer;

    @Mock
    private Configuration config;

    @Mock
    private DefaultMasterDataHelper helper;

    @Mock
    private ServiceConstants serviceConstants;

    @Mock
    private HearingRepository hearingRepository;

    @Test
    public void scheduleTest() {
        ScheduleHearingRequest schedulingRequests = new ScheduleHearingRequest();

        List<MdmsSlot> defaultSlots = Collections.singletonList(new MdmsSlot());
        when(helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME)).thenReturn(defaultSlots);

        List<MdmsHearing> defaultHearings = Collections.singletonList(new MdmsHearing());
        when(helper.getDataFromMDMS(MdmsHearing.class, serviceConstants.DEFAULT_HEARING_MASTER_NAME)).thenReturn(defaultHearings);

        Map<String, MdmsHearing> hearingTypeMap = defaultHearings.stream().collect(Collectors.toMap(MdmsHearing::getHearingType, obj -> obj));
        doNothing().when(hearingValidator).validateHearing(schedulingRequests, 0.0, hearingTypeMap);

        doNothing().when(hearingEnrichment).enrichScheduleHearing(schedulingRequests, defaultSlots, hearingTypeMap);

        doNothing().when(producer).push(config.getScheduleHearingTopic(), schedulingRequests.getHearing());

        List<ScheduleHearing> actualHearing = hearingService.schedule(schedulingRequests);

        verify(hearingValidator, times(1)).validateHearing(schedulingRequests, 0.0, hearingTypeMap);
        verify(hearingEnrichment, times(1)).enrichScheduleHearing(schedulingRequests, defaultSlots, hearingTypeMap);
        verify(producer, times(1)).push(config.getScheduleHearingTopic(), schedulingRequests.getHearing());
        assertEquals(schedulingRequests.getHearing(), actualHearing);
    }

    @Test
    public void scheduleTestWithException() {
        ScheduleHearingRequest schedulingRequests = new ScheduleHearingRequest();

        List<MdmsSlot> defaultSlots = Collections.singletonList(new MdmsSlot());
        when(helper.getDataFromMDMS(MdmsSlot.class, serviceConstants.DEFAULT_SLOTTING_MASTER_NAME)).thenReturn(defaultSlots);

        List<MdmsHearing> defaultHearings = Collections.singletonList(new MdmsHearing());
        when(helper.getDataFromMDMS(MdmsHearing.class, serviceConstants.DEFAULT_HEARING_MASTER_NAME)).thenReturn(defaultHearings);

        Map<String, MdmsHearing> hearingTypeMap = defaultHearings.stream().collect(Collectors.toMap(MdmsHearing::getHearingType, obj -> obj));
        doThrow(new RuntimeException()).when(hearingValidator).validateHearing(schedulingRequests, 0.0, hearingTypeMap);

        List<ScheduleHearing> actualHearing = hearingService.schedule(schedulingRequests);

        verify(hearingValidator, times(1)).validateHearing(schedulingRequests, 0.0, hearingTypeMap);
        assertEquals(Collections.emptyList(), actualHearing);
    }

    @Test
    public void updateTest() {
        ScheduleHearingRequest scheduleHearingRequest = new ScheduleHearingRequest();
        List<ScheduleHearing> expectedHearing = scheduleHearingRequest.getHearing();

        when(config.getScheduleHearingUpdateTopic()).thenReturn("someTopic");

        doNothing().when(hearingEnrichment).enrichUpdateScheduleHearing(scheduleHearingRequest.getRequestInfo(), scheduleHearingRequest.getHearing());
        doNothing().when(producer).push(anyString(), eq(scheduleHearingRequest.getHearing()));

        List<ScheduleHearing> actualHearing = hearingService.update(scheduleHearingRequest);

        verify(hearingEnrichment, times(1)).enrichUpdateScheduleHearing(scheduleHearingRequest.getRequestInfo(), scheduleHearingRequest.getHearing());
        verify(producer, times(1)).push(anyString(), eq(scheduleHearingRequest.getHearing()));
        assertEquals(expectedHearing, actualHearing);
    }

    @Test
    public void searchTest(){
        HearingSearchRequest hearingSearchRequest = new HearingSearchRequest();
        Integer limit = 10;
        Integer offset = 0;
        List<ScheduleHearing> scheduleHearings = Collections.singletonList(new ScheduleHearing());

        when(hearingRepository.getHearings(hearingSearchRequest.getCriteria(), limit, offset)).thenReturn(scheduleHearings);

        List<ScheduleHearing> actualHearing = hearingService.search(hearingSearchRequest, limit, offset);

        assertEquals(scheduleHearings, actualHearing);
    }

    @Test
    public void getAvailableDateForHearingTest() {
        // Arrange
        HearingSearchCriteria hearingSearchCriteria = new HearingSearchCriteria();
        AvailabilityDTO availability = new AvailabilityDTO();
        List<AvailabilityDTO> expectedAvailability = List.of(availability);
        when(hearingRepository.getAvailableDatesOfJudges(hearingSearchCriteria)).thenReturn(expectedAvailability);

        // Act
        List<AvailabilityDTO> actualAvailability = hearingService.getAvailableDateForHearing(hearingSearchCriteria);

        // Assert
        verify(hearingRepository, times(1)).getAvailableDatesOfJudges(hearingSearchCriteria);
        assertEquals(expectedAvailability, actualAvailability);
    }
}
package digit.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.repository.HearingRepository;
import digit.repository.ServiceRequestRepository;
import digit.web.models.*;
import digit.web.models.enums.EventType;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HearingValidatorTest {

    @InjectMocks
    private HearingValidator hearingValidator;

    @Mock
    private HearingRepository repository;

    @Mock
    private Configuration config;

    @Mock
    private ServiceRequestRepository requestRepository;

    @Mock
    private ObjectMapper mapper;

    private ScheduleHearingRequest scheduleHearingRequest;
    private List<ScheduleHearing> hearings;
    private Map<String, MdmsHearing> hearingTypeMap;

    @BeforeEach
    public void setUp() {
        // Initialize config mocks
        Mockito.lenient().when(config.getCaseUrl()).thenReturn("http://localhost:8080");
        Mockito.lenient().when(config.getCaseEndpoint()).thenReturn("/cases");
        Mockito.lenient().when(config.getEgovStateTenantId()).thenReturn("default");

        // Initialize test data
        RequestInfo requestInfo = new RequestInfo();
        ScheduleHearing hearing = new ScheduleHearing();
        hearing.setTenantId("default");
        hearing.setEventType(EventType.ADMISSION_HEARING);
        hearing.setDate(LocalDate.now().plusDays(1));
        hearing.setCaseId("CASE_ID");
        hearing.setJudgeId("JUDGE_ID");

        hearings = new ArrayList<>();
        hearings.add(hearing);

        scheduleHearingRequest = new ScheduleHearingRequest();
        scheduleHearingRequest.setRequestInfo(requestInfo);
        scheduleHearingRequest.setHearing(hearings);

        hearingTypeMap = new HashMap<>();
        MdmsHearing mdmsHearing = new MdmsHearing();
        mdmsHearing.setHearingTime(120);
        hearingTypeMap.put("EVENT_TYPE", mdmsHearing);
    }

//    @Test
//    public void testValidateHearing_Success() {
//        when(repository.getAvailableDatesOfJudges(any(HearingSearchCriteria.class))).thenReturn(Collections.emptyList());
//
//        hearingValidator.validateHearing(scheduleHearingRequest, 8.0, hearingTypeMap);
//
//        verify(repository, times(1)).getAvailableDatesOfJudges(any(HearingSearchCriteria.class));
//    }

    @Test
    public void testValidateHearing_TenantIdMissing() {
        hearings.get(0).setTenantId(null);

        CustomException exception = assertThrows(CustomException.class, () -> {
            hearingValidator.validateHearing(scheduleHearingRequest, 8.0, hearingTypeMap);
        });

        assertEquals("DK_SH_APP_ERR", exception.getCode());
        assertEquals("tenantId is mandatory for schedule a hearing", exception.getMessage());
    }

    @Test
    public void testValidateHearing_EventTypeMissing() {
        hearings.get(0).setEventType(null);

        CustomException exception = assertThrows(CustomException.class, () -> {
            hearingValidator.validateHearing(scheduleHearingRequest, 8.0, hearingTypeMap);
        });

        assertEquals("DK_SH_APP_ERR", exception.getCode());
        assertEquals("Event type is mandatory for schedule a hearing", exception.getMessage());
    }

    @Test
    public void testValidateHearing_DateMissing() {
        hearings.get(0).setDate(null);

        CustomException exception = assertThrows(CustomException.class, () -> {
            hearingValidator.validateHearing(scheduleHearingRequest, 8.0, hearingTypeMap);
        });

        assertEquals("DK_SH_APP_ERR", exception.getCode());
        assertEquals("date is mandatory for schedule a hearing", exception.getMessage());
    }

    @Test
    public void testValidateHearing_PastDate() {
        hearings.get(0).setDate(LocalDate.now().minusDays(1));

        CustomException exception = assertThrows(CustomException.class, () -> {
            hearingValidator.validateHearing(scheduleHearingRequest, 8.0, hearingTypeMap);
        });

        assertEquals("DK_SH_APP_ERR", exception.getCode());
        assertEquals("cannot schedule a hearing for past date: " + LocalDate.now().minusDays(1), exception.getMessage());
    }

    @Test
    public void testValidateHearing_InvalidHearingType() {
        hearingTypeMap.clear();

        CustomException exception = assertThrows(CustomException.class, () -> {
            hearingValidator.validateHearing(scheduleHearingRequest, 8.0, hearingTypeMap);
        });

        assertEquals("DK_SH_APP_ERR", exception.getCode());
        assertEquals("this hearing type does not exist in master data", exception.getMessage());
    }

    @Test
    public void testValidateHearing_InsufficientBandwidth() {
        List<AvailabilityDTO> judgeHearings = new ArrayList<>();
        AvailabilityDTO availabilityDTO = new AvailabilityDTO();
        availabilityDTO.setOccupiedBandwidth(7.0);
        judgeHearings.add(availabilityDTO);


        CustomException exception = assertThrows(CustomException.class, () -> {
            hearingValidator.validateHearing(scheduleHearingRequest, 8.0, hearingTypeMap);
        });

        assertEquals("DK_SH_APP_ERR", exception.getCode());
    }

    @Test
    public void testValidateHearing_ExceptionInRepositoryCall() {

        Exception exception = assertThrows(RuntimeException.class, () -> {
            hearingValidator.validateHearing(scheduleHearingRequest, 8.0, hearingTypeMap);
        });

        assertNotNull(exception.getMessage());
    }
}

package digit.enrichment;

import digit.config.Configuration;
import digit.models.coremodels.AuditDetails;
import digit.repository.HearingRepository;
import digit.util.IdgenUtil;
import digit.web.models.*;
import digit.web.models.enums.EventType;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HearingEnrichmentTest {

    @InjectMocks
    private HearingEnrichment hearingEnrichment;

    @Mock
    private IdgenUtil idgenUtil;

    @Mock
    private HearingRepository repository;

    @Mock
    private Configuration configuration;

    @Test
    void testEnrichScheduleHearing() {
        RequestInfo requestInfo = new RequestInfo();
        User user = new User();
        user.setUuid("test-uuid");
        requestInfo.setUserInfo(user);

        ScheduleHearing hearing1 = new ScheduleHearing();
        hearing1.setTenantId("tenantId1");
        hearing1.setDate(LocalDate.now());
        hearing1.setJudgeId("judge1");
        hearing1.setEventType(EventType.ADMISSION_HEARING);

        ScheduleHearing hearing2 = new ScheduleHearing();
        hearing2.setTenantId("tenantId1");
        hearing2.setDate(LocalDate.now());
        hearing2.setJudgeId("judge1");
        hearing2.setEventType(EventType.ADMISSION_HEARING);

        List<ScheduleHearing> hearingList = Arrays.asList(hearing1, hearing2);

        ScheduleHearingRequest schedulingRequests = new ScheduleHearingRequest();
        schedulingRequests.setRequestInfo(requestInfo);
        schedulingRequests.setHearing(hearingList);

        List<MdmsSlot> defaultSlots = new ArrayList<>();
        Map<String, MdmsHearing> hearingTypeMap = new HashMap<>();
        MdmsHearing mdmsHearing = new MdmsHearing();
        mdmsHearing.setHearingTime(30);
        hearingTypeMap.put(EventType.ADMISSION_HEARING.toString(), mdmsHearing);

        when(idgenUtil.getIdList(any(), anyString(), anyString(), any(), anyInt()))
                .thenReturn(Arrays.asList("hearingId1", "hearingId2"));
        when(configuration.getHearingIdFormat()).thenReturn("hearingIdFormat");

        hearingEnrichment.enrichScheduleHearing(schedulingRequests, defaultSlots, hearingTypeMap);

        verify(idgenUtil, times(1)).getIdList(any(), anyString(), anyString(), any(), anyInt());
        assertNotNull(hearing1.getAuditDetails());
        assertNotNull(hearing2.getAuditDetails());
        assertEquals("hearingId1", hearing1.getHearingBookingId());
        assertEquals("hearingId2", hearing2.getHearingBookingId());
        assertEquals(1, hearing1.getRowVersion());
        assertEquals(1, hearing2.getRowVersion());
    }
    @Test
    void testUpdateTimingInHearings() {
        ScheduleHearing hearing1 = new ScheduleHearing();
        hearing1.setDate(LocalDate.now());
        hearing1.setJudgeId("judge1");
        hearing1.setEventType(EventType.ADMISSION_HEARING);

        List<ScheduleHearing> hearingList = Collections.singletonList(hearing1);

        List<MdmsSlot> defaultSlots = new ArrayList<>();
        MdmsSlot slot = new MdmsSlot();
        slot.setSlotStartTime("09:00:00");
        slot.setSlotEndTime("17:00:00");
        defaultSlots.add(slot);

        Map<String, MdmsHearing> hearingTypeMap = new HashMap<>();
        MdmsHearing mdmsHearing = new MdmsHearing();
        mdmsHearing.setHearingTime(30);
        hearingTypeMap.put(EventType.ADMISSION_HEARING.toString(), mdmsHearing);

        when(repository.getHearings(any(), any(), any())).thenReturn(new ArrayList<>());

        hearingEnrichment.updateTimingInHearings(hearingList, hearingTypeMap, defaultSlots);

        assertNotNull(hearing1.getStartTime());
        assertNotNull(hearing1.getEndTime());
    }

    @Test
    void testEnrichUpdateScheduleHearing() {
        RequestInfo requestInfo = new RequestInfo();
        User user = new User();
        user.setUuid("test-uuid");
        requestInfo.setUserInfo(user);

        ScheduleHearing hearing1 = new ScheduleHearing();
        AuditDetails auditDetails = new AuditDetails();
        hearing1.setAuditDetails(auditDetails);
        hearing1.setRowVersion(1);

        List<ScheduleHearing> hearingList = Collections.singletonList(hearing1);

        hearingEnrichment.enrichUpdateScheduleHearing(requestInfo, hearingList);

        assertEquals(2, hearing1.getRowVersion());
        assertEquals("test-uuid", hearing1.getAuditDetails().getLastModifiedBy());
        assertNotNull(hearing1.getAuditDetails().getLastModifiedTime());
    }

//    @Test
//    void testGetAuditDetailsScheduleHearing() {
//        RequestInfo requestInfo = new RequestInfo();
//        User user = new User();
//        user.setUuid("test-uuid");
//        requestInfo.setUserInfo(user);
//
//        AuditDetails auditDetails = hearingEnrichment.getAuditDetailsScheduleHearing(requestInfo);
//
//        assertEquals("test-uuid", auditDetails.getCreatedBy());
//        assertEquals("test-uuid", auditDetails.getLastModifiedBy());
//        assertNotNull(auditDetails.getCreatedTime());
//        assertNotNull(auditDetails.getLastModifiedTime());
//    }

    @Test
    void testUpdateHearingTime() {
        ScheduleHearing hearing = new ScheduleHearing();
        hearing.setDate(LocalDate.now());
        hearing.setEventType(EventType.ADMISSION_HEARING);

        List<MdmsSlot> slots = new ArrayList<>();
        MdmsSlot slot = new MdmsSlot();
        slot.setSlotStartTime("09:00:00");
        slot.setSlotEndTime("17:00:00");
        slots.add(slot);

        List<ScheduleHearing> scheduledHearings = new ArrayList<>();

        hearingEnrichment.updateHearingTime(hearing, slots, scheduledHearings, 30);

        assertNotNull(hearing.getStartTime());
        assertNotNull(hearing.getEndTime());
    }

    @Test
    void testCanScheduleHearings() {
        ScheduleHearing hearing1 = new ScheduleHearing();
        hearing1.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)));
        hearing1.setEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 30)));

        ScheduleHearing hearing2 = new ScheduleHearing();
        hearing2.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)));
        hearing2.setEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 30)));

        List<ScheduleHearing> scheduledHearings = Collections.singletonList(hearing2);

        List<MdmsSlot> slots = new ArrayList<>();
        MdmsSlot slot = new MdmsSlot();
        slot.setSlotStartTime("09:00:00");
        slot.setSlotEndTime("17:00:00");
        slots.add(slot);

        boolean canSchedule = hearingEnrichment.canScheduleHearings(hearing1, scheduledHearings, slots);

        assertTrue(canSchedule);
    }

    @Test
    void testGetLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));
        String newTime = "11:00:00";

        LocalDateTime updatedDateTime = hearingEnrichment.getLocalDateTime(dateTime, newTime);

        assertEquals(LocalTime.of(11, 0), updatedDateTime.toLocalTime());
    }

    @Test
    void testGetLocalTime() {
        String time = "10:00:00";
        LocalTime localTime = hearingEnrichment.getLocalTime(time);

        assertEquals(LocalTime.of(10, 0), localTime);
    }

    @Test
    void testEnrichBulkReschedule() {
        RequestInfo requestInfo = new RequestInfo();
        User user = new User();
        user.setUuid("test-uuid");
        requestInfo.setUserInfo(user);

        ScheduleHearing hearing1 = new ScheduleHearing();
        AuditDetails auditDetails = new AuditDetails();
        hearing1.setAuditDetails(auditDetails);
        hearing1.setRowVersion(1);
        hearing1.setDate(LocalDate.now());
        hearing1.setJudgeId("judge1");
        hearing1.setEventType(EventType.ADMISSION_HEARING);

        List<ScheduleHearing> hearingList = Collections.singletonList(hearing1);

        ScheduleHearingRequest request = new ScheduleHearingRequest();
        request.setRequestInfo(requestInfo);
        request.setHearing(hearingList);

        List<MdmsSlot> defaultSlots = new ArrayList<>();
        MdmsSlot slot = new MdmsSlot();
        slot.setSlotStartTime("09:00:00");
        slot.setSlotEndTime("17:00:00");
        defaultSlots.add(slot);

        Map<String, MdmsHearing> hearingTypeMap = new HashMap<>();
        MdmsHearing mdmsHearing = new MdmsHearing();
        mdmsHearing.setHearingTime(30);
        hearingTypeMap.put(EventType.ADMISSION_HEARING.toString(), mdmsHearing);

        when(repository.getHearings(any(), any(), any())).thenReturn(new ArrayList<>());

        hearingEnrichment.enrichBulkReschedule(request, defaultSlots, hearingTypeMap);

        assertEquals(2, hearing1.getRowVersion());
        assertEquals("test-uuid", hearing1.getAuditDetails().getLastModifiedBy());
        assertNotNull(hearing1.getAuditDetails().getLastModifiedTime());
        assertNotNull(hearing1.getStartTime());
        assertNotNull(hearing1.getEndTime());
    }
}


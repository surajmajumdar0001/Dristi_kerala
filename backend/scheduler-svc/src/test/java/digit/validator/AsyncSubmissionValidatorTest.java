package digit.validator;

import digit.config.Configuration;
import digit.repository.HearingRepository;
import digit.web.models.AsyncSubmission;
import digit.web.models.HearingSearchCriteria;
import digit.web.models.ScheduleHearing;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AsyncSubmissionValidatorTest {

    @Mock
    private HearingRepository repository;

    @Mock
    private Configuration config;

    @InjectMocks
    private AsyncSubmissionValidator validator;

    private AsyncSubmission asyncSubmission;
    private ScheduleHearing hearing;

    @BeforeEach
    public void setup() {
        asyncSubmission = new AsyncSubmission();
        asyncSubmission.setTenantId("tenant1");
        asyncSubmission.setCaseId("case1");
        asyncSubmission.setSubmissionDate(LocalDate.now().plusDays(2).toString());
        asyncSubmission.setResponseDate(LocalDate.now().plusDays(3).toString());

        hearing = new ScheduleHearing();
        hearing.setDate(LocalDate.now().plusDays(10));
        hearing.setStartTime(LocalDateTime.now().plusDays(10));
    }

    @Test
    public void testValidateSubmissionDates_TenantIdNull() {
        asyncSubmission.setTenantId(null);

        CustomException exception = assertThrows(CustomException.class, () -> validator.validateSubmissionDates(asyncSubmission));
        assertEquals("DK_ASH_APP_ERR", exception.getCode());
        assertEquals("Tenant id is either null or invalid", exception.getMessage());
    }

    @Test
    public void testValidateSubmissionDates_InvalidTenantId() {
        asyncSubmission.setTenantId("invalidTenant");
        when(config.getEgovStateTenantId()).thenReturn("tenant1");

        CustomException exception = assertThrows(CustomException.class, () -> validator.validateSubmissionDates(asyncSubmission));
        assertEquals("DK_ASH_APP_ERR", exception.getCode());
        assertEquals("Tenant id is either null or invalid", exception.getMessage());
    }

    @Test
    public void testValidateSubmissionDates_ValidDates() {
        when(config.getEgovStateTenantId()).thenReturn("tenant1");
        when(repository.getHearings(any(HearingSearchCriteria.class), any(), any())).thenReturn(Collections.singletonList(hearing));

        assertDoesNotThrow(() -> validator.validateSubmissionDates(asyncSubmission));
    }

    @Test
    public void testValidateSubmissionDates_ResponseDateAfterHearingDate() {
        asyncSubmission.setResponseDate(LocalDate.now().plusDays(11).toString());
        when(config.getEgovStateTenantId()).thenReturn("tenant1");
        when(repository.getHearings(any(HearingSearchCriteria.class), any(), any())).thenReturn(Collections.singletonList(hearing));

        CustomException exception = assertThrows(CustomException.class, () -> validator.validateSubmissionDates(asyncSubmission));
        assertEquals("DK_ASH_APP_ERR", exception.getCode());
        assertEquals("Async submission and response dates must be before hearing date", exception.getMessage());
    }

//    @Test
//    public void testValidateSubmissionAndResponseDates_InvalidSubmissionDate() {
//        asyncSubmission.setSubmissionDate(LocalDate.now().minusDays(1).toString());
//
//        CustomException exception = assertThrows(CustomException.class, () -> validator.validateSubmissionAndResponseDates(asyncSubmission));
//        assertEquals("DK_ASR_APP_ERR", exception.getCode());
//        assertEquals("Submission date must be after current date", exception.getMessage());
//    }

//    @Test
//    public void testValidateSubmissionAndResponseDates_InvalidResponseDate() {
//        asyncSubmission.setResponseDate(LocalDate.now().minusDays(1).toString());
//
//        CustomException exception = assertThrows(CustomException.class, () -> validator.validateSubmissionAndResponseDates(asyncSubmission));
//        assertEquals("DK_ASR_APP_ERR", exception.getCode());
//        assertEquals("Response date must be after current date", exception.getMessage());
//    }

//    @Test
//    public void testValidateSubmissionAndResponseDates_ResponseDateBeforeSubmissionDate() {
//        asyncSubmission.setResponseDate(LocalDate.now().plusDays(1).toString());
//        asyncSubmission.setSubmissionDate(LocalDate.now().plusDays(2).toString());
//
//        CustomException exception = assertThrows(CustomException.class, () -> validator.validateSubmissionAndResponseDates(asyncSubmission));
//        assertEquals("DK_ASR_APP_ERR", exception.getCode());
//        assertEquals("Submission date must be before Response date", exception.getMessage());
//    }
//
//    @Test
//    public void testValidateSubmissionAndResponseDates_ValidDates() {
//        assertDoesNotThrow(() -> validator.validateSubmissionAndResponseDates(asyncSubmission));
//    }

    @Test
    public void testFindLatestHearingByHearingDate_EmptyList() {
        assertTrue(validator.findLatestHearingByHearingDate(Collections.emptyList()).isEmpty());
    }

    @Test
    public void testFindLatestHearingByHearingDate_NoValidHearings() {
        hearing.setDate(LocalDate.now().minusDays(1));
        assertTrue(validator.findLatestHearingByHearingDate(Collections.singletonList(hearing)).isEmpty());
    }

    @Test
    public void testFindLatestHearingByHearingDate_ValidHearing() {
        assertTrue(validator.findLatestHearingByHearingDate(Collections.singletonList(hearing)).isPresent());
    }

    @Test
    public void testValidateDates_ValidDates() {
        when(repository.getHearings(any(HearingSearchCriteria.class), any(), any())).thenReturn(Collections.singletonList(hearing));

        validator.validateDates(asyncSubmission);

        assertEquals(LocalDate.now().plusDays(3).toString(), asyncSubmission.getResponseDate());
    }

    @Test
    public void testValidateDates_ResponseDateAfterHearingDate() {
        asyncSubmission.setResponseDate(LocalDate.now().plusDays(11).toString());
        when(repository.getHearings(any(HearingSearchCriteria.class), any(), any())).thenReturn(Collections.singletonList(hearing));

        validator.validateDates(asyncSubmission);

        assertEquals(LocalDate.now().plusDays(9).toString(), asyncSubmission.getResponseDate());
    }

    @Test
    public void testValidateDates_DaysUntilHearingLessThan6() {
        hearing.setDate(LocalDate.now().plusDays(5));
        when(repository.getHearings(any(HearingSearchCriteria.class), any(), any())).thenReturn(Collections.singletonList(hearing));

        validator.validateDates(asyncSubmission);

        assertEquals(LocalDate.now().plusDays(1).toString(), asyncSubmission.getSubmissionDate());
    }
}

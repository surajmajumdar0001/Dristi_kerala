package digit.web.controllers;


import digit.service.HearingService;
import digit.web.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HearingControllerTest {

    @Mock
    private HearingService hearingService;

    @InjectMocks
    private HearingApiController hearingApiController;

    @Test
    public void testScheduleHearing() {
        ScheduleHearingRequest scheduleHearingRequest = new ScheduleHearingRequest();
        ScheduleHearing scheduleHearing  = new ScheduleHearing();
        scheduleHearing.setTenantId("tenantId");
        scheduleHearing.setJudgeId("judgeId");
        scheduleHearing.setCaseId("caseId");
        List<ScheduleHearing> scheduleHearings = List.of(scheduleHearing);

        when(hearingService.schedule(scheduleHearingRequest)).thenReturn(scheduleHearings);

        ResponseEntity<HearingResponse> response = hearingApiController.scheduleHearing(scheduleHearingRequest);

        assertEquals(scheduleHearings, response.getBody().getHearings());
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void testSearchHearing() {
        // Test case for searchHearing
        HearingSearchRequest request = new HearingSearchRequest();
        HearingSearchCriteria criteria = HearingSearchCriteria.builder().judgeId("judgeId").tenantId("tenantId").build();
        Integer limit = 10;
        Integer offset = 0;
        request.setCriteria(criteria);

        ScheduleHearing scheduleHearing = ScheduleHearing.builder().judgeId("judgeId").tenantId("tenantId").hearingBookingId("HR001").build();

        List<ScheduleHearing> scheduleHearings = List.of(scheduleHearing);
        when(hearingService.search(request, limit, offset)).thenReturn(scheduleHearings);
        ResponseEntity<HearingResponse> response = hearingApiController.searchHearing(request, limit, offset);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(scheduleHearings, response.getBody().getHearings());
    }
}
package digit.web.controllers;


import digit.web.models.HearingResponse;
import digit.web.models.HearingSearchRequest;
import digit.web.models.ScheduleHearingRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HearingControllerTest {

    @InjectMocks
    private HearingApiController hearingApiController;

    @Test
    public void testScheduleHearing() {
        // Test case for scheduleHearing
        ScheduleHearingRequest request = new ScheduleHearingRequest();
        ResponseEntity<HearingResponse> response = hearingApiController.scheduleHearing(request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void testSearchHearing() {
        // Test case for searchHearing
        HearingSearchRequest request = new HearingSearchRequest();
        Integer limit = 10;
        Integer offset = 1;
        ResponseEntity<HearingResponse> response = hearingApiController.searchHearing(request, limit, offset);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }
}

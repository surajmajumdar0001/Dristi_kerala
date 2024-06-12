package digit.web.controllers;

import digit.TestConfiguration;
import digit.service.HearingService;
import digit.util.ResponseInfoFactory;
import digit.web.models.HearingResponse;
import digit.web.models.HearingSearchRequest;
import digit.web.models.ScheduleHearing;
import digit.web.models.ScheduleHearingRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebMvcTest(JudgeCalendarApiController.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class HearingApiControllerTest {

    @InjectMocks
    private HearingApiController hearingApiController;

    @Mock
    private HearingService hearingService;

    @Mock
    private ResponseInfoFactory responseInfoFactory;

    @Test
    public void scheduleHearingTest() {
        // Arrange
        ScheduleHearingRequest request = new ScheduleHearingRequest();
        ScheduleHearing hearing = new ScheduleHearing();
        List<ScheduleHearing> scheduledHearings = List.of(hearing); // populate this list with expected data
        HearingResponse expectedResponse = HearingResponse.builder().hearings(scheduledHearings).responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true)).build();

        when(hearingService.schedule(request)).thenReturn(scheduledHearings);

        // Act
        ResponseEntity<HearingResponse> actualResponseEntity = hearingApiController.scheduleHearing(request);

        // Assert
        verify(hearingService, times(1)).schedule(request);
        assertEquals(HttpStatus.ACCEPTED, actualResponseEntity.getStatusCode());
        assertEquals(expectedResponse, actualResponseEntity.getBody());
    }

    @Test
    public void searchHearingTest(){
        HearingSearchRequest request = new HearingSearchRequest();
        ScheduleHearing hearing = new ScheduleHearing();
        List<ScheduleHearing> scheduledHearings = List.of(hearing);
        RequestInfo requestInfo = new RequestInfo();
        request.setRequestInfo(requestInfo);
        Integer limit = 10;
        Integer offset = 0;
        when(hearingService.search(request, limit, offset)).thenReturn(scheduledHearings);
        when(responseInfoFactory.createResponseInfo(requestInfo, true)).thenReturn(new ResponseInfo());

        ResponseEntity<HearingResponse> hearingResponse = hearingApiController.searchHearing(request, limit, offset);

        assertEquals(HttpStatus.OK, hearingResponse.getStatusCode());
        assertEquals(scheduledHearings, hearingResponse.getBody().getHearings());
    }
}

package digit.web.controllers;

import digit.TestConfiguration;
import digit.service.ReScheduleHearingService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(ReScheduleHearingController.class)
@Import(TestConfiguration.class)
public class ReScheduleHearingControllerTest {

        @InjectMocks
        private ReScheduleHearingController reScheduleHearingController;

        @Mock
        private ReScheduleHearingService reScheduleHearingService;

        @Mock
        private ResponseInfoFactory responseInfoFactory;

    @Test
    public void reScheduleHearingTest(){
        ReScheduleHearingRequest request = new ReScheduleHearingRequest();
        ReScheduleHearing reScheduleHearing = new ReScheduleHearing();
        List<ReScheduleHearing> reScheduleHearings = List.of(reScheduleHearing);
        RequestInfo requestInfo = new RequestInfo();
        request.setRequestInfo(requestInfo);
        when(reScheduleHearingService.create(request)).thenReturn(reScheduleHearings);
        when(responseInfoFactory.createResponseInfo(requestInfo, true)).thenReturn(new ResponseInfo());

        ResponseEntity<ReScheduleHearingResponse> reScheduleHearingResponse = reScheduleHearingController.reScheduleHearing(request);

        assertEquals(HttpStatus.OK, reScheduleHearingResponse.getStatusCode());
        assertEquals(reScheduleHearings, reScheduleHearingResponse.getBody().getReScheduleHearings());
    }

    @Test
    public void updateReScheduleHearingTest(){
        ReScheduleHearingRequest request = new ReScheduleHearingRequest();
        ReScheduleHearing reScheduleHearing = new ReScheduleHearing();
        List<ReScheduleHearing> reScheduleHearings = List.of(reScheduleHearing);
        RequestInfo requestInfo = new RequestInfo();
        request.setRequestInfo(requestInfo);
        when(reScheduleHearingService.update(request)).thenReturn(reScheduleHearings);
        when(responseInfoFactory.createResponseInfo(requestInfo, true)).thenReturn(new ResponseInfo());

        ResponseEntity<ReScheduleHearingResponse> reScheduleHearingResponse = reScheduleHearingController.updateReScheduleHearing(request);

        assertEquals(HttpStatus.OK, reScheduleHearingResponse.getStatusCode());
        assertEquals(reScheduleHearings, reScheduleHearingResponse.getBody().getReScheduleHearings());
    }

    @Test
    public void searchRescheduleHearingTest(){
        ReScheduleHearingReqSearchRequest request = new ReScheduleHearingReqSearchRequest();
        ReScheduleHearing reScheduleHearing = new ReScheduleHearing();
        List<ReScheduleHearing> reScheduleHearings = List.of(reScheduleHearing);
        RequestInfo requestInfo = new RequestInfo();
        request.setRequestInfo(requestInfo);
        Integer limit = 10;
        Integer offset = 0;
        when(reScheduleHearingService.search(request, limit, offset)).thenReturn(reScheduleHearings);
        when(responseInfoFactory.createResponseInfo(requestInfo, true)).thenReturn(new ResponseInfo());

        ResponseEntity<ReScheduleHearingResponse> reScheduleHearingResponse = reScheduleHearingController.searchRescheduleHearing(request, limit, offset);

        assertEquals(HttpStatus.OK, reScheduleHearingResponse.getStatusCode());
        assertEquals(reScheduleHearings, reScheduleHearingResponse.getBody().getReScheduleHearings());
    }

    @Test
    public void bulkRescheduleHearingTest(){
        BulkReScheduleHearingRequest request = new BulkReScheduleHearingRequest();
        ReScheduleHearing reScheduleHearing = new ReScheduleHearing();
        List<ReScheduleHearing> reScheduleHearings = List.of(reScheduleHearing);
        RequestInfo requestInfo = new RequestInfo();
        request.setRequestInfo(requestInfo);
        when(reScheduleHearingService.bulkReschedule(request)).thenReturn(reScheduleHearings);
        when(responseInfoFactory.createResponseInfo(requestInfo, true)).thenReturn(new ResponseInfo());

        ResponseEntity<ReScheduleHearingResponse> reScheduleHearingResponse = reScheduleHearingController.bulkRescheduleHearing(request);

        assertEquals(HttpStatus.OK, reScheduleHearingResponse.getStatusCode());
        assertEquals(reScheduleHearings, reScheduleHearingResponse.getBody().getReScheduleHearings());
    }
}

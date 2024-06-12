package digit.web.controllers;

import digit.TestConfiguration;
import digit.service.RescheduleRequestOptOutService;
import digit.util.ResponseInfoFactory;
import digit.web.models.OptOut;
import digit.web.models.OptOutRequest;
import digit.web.models.OptOutResponse;
import digit.web.models.OptOutSearchRequest;
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
public class RescheduleRequestOptOutApiControllerTest {

    @Mock
    private RescheduleRequestOptOutService optOutService;

    @Mock
    private ResponseInfoFactory responseInfoFactory;

    @InjectMocks
    private RescheduleRequestOptOutApiController optOutController;

    @Test
    public void optOutDatesTest() {
        OptOutRequest request = new OptOutRequest();
        OptOut optOut = new OptOut();
        List<OptOut> optOuts = List.of(optOut);
        RequestInfo requestInfo = new RequestInfo();
        request.setRequestInfo(requestInfo);

        when(optOutService.create(request)).thenReturn(optOuts);
        when(responseInfoFactory.createResponseInfo(requestInfo, true)).thenReturn(new ResponseInfo());

        ResponseEntity<OptOutResponse> optOutResponse = optOutController.optOutDates(request);

        assertEquals(HttpStatus.OK, optOutResponse.getStatusCode());
        assertEquals( optOuts, optOutResponse.getBody().getOptOuts());
    }

    @Test
    public void updateOptOutTest() {
        OptOutRequest request = new OptOutRequest();
        OptOut optOut = new OptOut();
        List<OptOut> optOuts = List.of(optOut);
        RequestInfo requestInfo = new RequestInfo();
        request.setRequestInfo(requestInfo);

        when(optOutService.update(request)).thenReturn(optOuts);
        when(responseInfoFactory.createResponseInfo(requestInfo, true)).thenReturn(new ResponseInfo());

        ResponseEntity<OptOutResponse> optOutResponse = optOutController.updateOptOut(request);

        assertEquals(HttpStatus.OK, optOutResponse.getStatusCode());
        assertEquals( optOuts, optOutResponse.getBody().getOptOuts());
    }

    @Test
    public void searchOptOutTest() {
        OptOutSearchRequest request = new OptOutSearchRequest();
        OptOut optOut = new OptOut();
        List<OptOut> optOuts = List.of(optOut);
        RequestInfo requestInfo = new RequestInfo();
        request.setRequestInfo(requestInfo);

        when(optOutService.search(request, 10, 1)).thenReturn(optOuts);
        when(responseInfoFactory.createResponseInfo(requestInfo, true)).thenReturn(new ResponseInfo());

        ResponseEntity<OptOutResponse> optOutResponse = optOutController.searchOptOut(request, 0, 1);

        assertEquals(HttpStatus.OK, optOutResponse.getStatusCode());
        assertEquals( optOuts, optOutResponse.getBody().getOptOuts());
    }
}

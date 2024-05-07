package digit.web.controllers;


import digit.service.RescheduleRequestOptOutService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("optOutApiController")
@RequestMapping("")
public class OptOutApiController {

    @Autowired
    private RescheduleRequestOptOutService rescheduleRequestOptOutService;

    @RequestMapping(value = "/hearing/v1/_opt-out", method = RequestMethod.POST)
    public ResponseEntity<OptOutResponse> optOutDates(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody OptOutRequest request) {
        //service call
        List<OptOut> optOutResponse = rescheduleRequestOptOutService.create(request);

        OptOutResponse response = OptOutResponse.builder().responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .optOuts(optOutResponse).build();

        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/hearing/v1/opt-out/_update", method = RequestMethod.POST)
    public ResponseEntity<OptOutResponse> updateOptOut(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody OptOutRequest request) {
        //service call
        List<OptOut> optOutResponse = rescheduleRequestOptOutService.update(request);

        OptOutResponse response = OptOutResponse.builder().responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .optOuts(optOutResponse).build();

        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/hearing/v1/opt-out/_search", method = RequestMethod.POST)
    public ResponseEntity<OptOutResponse> searchOptOut(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody OptOutSearchRequest request) {
        //service call
        List<OptOut> optOuts = rescheduleRequestOptOutService.search(request);

        OptOutResponse response = OptOutResponse.builder().responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .optOuts(optOuts).build();

        return ResponseEntity.accepted().body(response);
    }
}

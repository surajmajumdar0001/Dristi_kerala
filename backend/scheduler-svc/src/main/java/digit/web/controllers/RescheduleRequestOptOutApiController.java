package digit.web.controllers;


import digit.service.RescheduleRequestOptOutService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController("rescheduleRequestOptOutApiController")
@RequestMapping("")
public class RescheduleRequestOptOutApiController {

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
    public ResponseEntity<OptOutResponse> searchOptOut(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody OptOutSearchRequest request, @NotNull @Min(0) @Max(1000) @ApiParam(value = "Pagination - limit records in response", required = true) @Valid @RequestParam(value = "limit", required = true) Integer limit, @NotNull @Min(1) @ApiParam(value = "Pagination - offset for which response is returned", required = true) @Valid @RequestParam(value = "offset", required = true) Integer offset) {
        //service call
        List<OptOut> optOuts = rescheduleRequestOptOutService.search(request, limit, offset);

        OptOutResponse response = OptOutResponse.builder().responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .optOuts(optOuts).build();

        return ResponseEntity.accepted().body(response);
    }
}

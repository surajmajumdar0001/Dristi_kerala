package digit.web.controllers;


import digit.util.ResponseInfoFactory;
import digit.web.models.HearingResponse;


import digit.web.models.ScheduleHearingRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-15T13:15:39.759211883+05:30[Asia/Kolkata]")
@RestController("hearingApiController")
@RequestMapping("/hearing")
public class HearingApiController {


    @RequestMapping(value = "/v1/_schedule", method = RequestMethod.POST)
    public ResponseEntity<HearingResponse> scheduleHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody ScheduleHearingRequest request) {
        //call service here

        HearingResponse response = HearingResponse.builder().hearing(new ArrayList<>()).responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true)).build();

        return ResponseEntity.accepted().body(response);
    }
}

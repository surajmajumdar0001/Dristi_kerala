package digit.web.controllers;


import digit.service.HearingService;
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

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-15T13:15:39.759211883+05:30[Asia/Kolkata]")
@RestController("hearingApiController")
@RequestMapping("")
public class HearingApiController {


    @Autowired
    private HearingService hearingService;


    @RequestMapping(value = "/hearing/v1/_schedule", method = RequestMethod.POST)
    public ResponseEntity<HearingResponse> scheduleHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody ScheduleHearingRequest request) {

        List<ScheduleHearing> scheduledHearings = hearingService.scheduleHearing(request);
        HearingResponse response = HearingResponse.builder().hearings(scheduledHearings).responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true)).build();
        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/hearing/v1/_reschedule", method = RequestMethod.POST)
    public ResponseEntity<ReScheduleHearingResponse> reScheduleHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody ReScheduleHearingRequest request) {

        List<ReScheduleHearing> scheduledHearings = hearingService.reScheduleHearingRequest(request.getReScheduleHearing());

        ReScheduleHearingResponse response = ReScheduleHearingResponse.builder().responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .reScheduleHearings(scheduledHearings).build();

        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/hearing/v1/_search", method = RequestMethod.POST)
    public ResponseEntity<ReScheduleHearingResponse> searchHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody HearingSearchRequest request) {

        List<ReScheduleHearing> scheduledHearings = hearingService.search(request);

        ReScheduleHearingResponse response = ReScheduleHearingResponse.builder().responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .reScheduleHearings(scheduledHearings).build();

        return ResponseEntity.accepted().body(response);
    }
}

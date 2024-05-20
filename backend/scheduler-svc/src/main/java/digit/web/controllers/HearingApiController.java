package digit.web.controllers;


import digit.service.HearingService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class HearingApiController {

    private final HearingService hearingService;

    @Autowired
    public HearingApiController(HearingService hearingService) {
        this.hearingService = hearingService;
    }


    @RequestMapping(value = "/hearing/v1/_schedule", method = RequestMethod.POST)
    public ResponseEntity<HearingResponse> scheduleHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody ScheduleHearingRequest request) {
        log.info("api=/hearing/v1/_schedule, result = IN_PROGRESS");
        List<ScheduleHearing> scheduledHearings = hearingService.schedule(request);
        HearingResponse response = HearingResponse.builder().hearings(scheduledHearings).responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true)).build();
        log.info("api=/hearing/v1/_schedule, result = SUCCESS");
        return ResponseEntity.accepted().body(response);
    }


    @RequestMapping(value = "/hearing/v1/_search", method = RequestMethod.POST)
    public ResponseEntity<HearingResponse> searchHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody HearingSearchRequest request) {
        log.info("api=/hearing/v1/_search, result = IN_PROGRESS");
        List<ScheduleHearing> scheduledHearings = hearingService.search(request);
        HearingResponse response = HearingResponse.builder().responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .hearings(scheduledHearings).build();
        log.info("api=/hearing/v1/_search, result = SUCCESS");
        return ResponseEntity.accepted().body(response);
    }

}

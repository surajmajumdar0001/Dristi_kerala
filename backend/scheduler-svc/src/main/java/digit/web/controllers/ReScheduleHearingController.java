package digit.web.controllers;


import digit.service.ReScheduleHearingService;
import digit.util.ResponseInfoFactory;
import digit.web.models.ReScheduleHearing;
import digit.web.models.ReScheduleHearingReqSearchRequest;
import digit.web.models.ReScheduleHearingRequest;
import digit.web.models.ReScheduleHearingResponse;
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

import java.util.Collections;
import java.util.List;

@RestController("reScheduleHearingApiController")
@RequestMapping("")
public class ReScheduleHearingController {

    @Autowired
    private ReScheduleHearingService reScheduleHearingService;

    @RequestMapping(value = "/hearing/v1/_reschedule", method = RequestMethod.POST)
    public ResponseEntity<ReScheduleHearingResponse> reScheduleHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody ReScheduleHearingRequest request) {
        //service call
        List<ReScheduleHearing> scheduledHearings = reScheduleHearingService.create(request);

        ReScheduleHearingResponse response = ReScheduleHearingResponse.builder().ResponseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .reScheduleHearings(scheduledHearings).build();

        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/hearing/v1/reschedule/_update", method = RequestMethod.POST)
    public ResponseEntity<ReScheduleHearingResponse> updateReScheduleHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody ReScheduleHearingRequest request) {
        //service call
        ReScheduleHearing scheduledHearings = reScheduleHearingService.update(request);

        ReScheduleHearingResponse response = ReScheduleHearingResponse.builder().ResponseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .reScheduleHearings(Collections.singletonList(scheduledHearings)).build();

        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/hearing/v1/reschedule/_search", method = RequestMethod.POST)
    public ResponseEntity<ReScheduleHearingResponse> searchRescheduleHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody ReScheduleHearingReqSearchRequest request) {
        //service call
        List<ReScheduleHearing> scheduledHearings = reScheduleHearingService.search(request);

        ReScheduleHearingResponse response = ReScheduleHearingResponse.builder().ResponseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .reScheduleHearings(scheduledHearings).build();

        return ResponseEntity.accepted().body(response);
    }
}

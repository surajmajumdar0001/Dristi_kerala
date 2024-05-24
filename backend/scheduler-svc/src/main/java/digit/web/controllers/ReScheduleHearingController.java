package digit.web.controllers;


import digit.service.ReScheduleHearingService;
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

import java.util.Collections;
import java.util.List;

@RestController("reScheduleHearingApiController")
@RequestMapping("")
@Slf4j
public class ReScheduleHearingController {

    @Autowired
    private ReScheduleHearingService reScheduleHearingService;

    @RequestMapping(value = "/hearing/v1/_reschedule", method = RequestMethod.POST)
    public ResponseEntity<ReScheduleHearingResponse> reScheduleHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody ReScheduleHearingRequest request) {
        log.info("api = /hearing/v1/_reschedule, result = IN_PROGRESS");
        List<ReScheduleHearing> scheduledHearings = reScheduleHearingService.create(request);
        ReScheduleHearingResponse response = ReScheduleHearingResponse.builder().ResponseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .reScheduleHearings(scheduledHearings).build();
        log.info("api = /hearing/v1/_reschedule, result = SUCCESS");
        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/hearing/v1/reschedule/_update", method = RequestMethod.POST)
    public ResponseEntity<ReScheduleHearingResponse> updateReScheduleHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody ReScheduleHearingRequest request) {
        log.info("api = /hearing/v1/reschedule/_update, result = IN_PROGRESS");
        List<ReScheduleHearing> scheduledHearings = reScheduleHearingService.update(request);
        ReScheduleHearingResponse response = ReScheduleHearingResponse.builder().ResponseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .reScheduleHearings(scheduledHearings).build();
        log.info("api = /hearing/v1/reschedule/_update, result = SUCCESS");
        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/hearing/v1/reschedule/_search", method = RequestMethod.POST)
    public ResponseEntity<ReScheduleHearingResponse> searchRescheduleHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody ReScheduleHearingReqSearchRequest request) {
        log.info("api = /hearing/v1/reschedule/_search, result = IN_PROGRESS");
        List<ReScheduleHearing> scheduledHearings = reScheduleHearingService.search(request);
        ReScheduleHearingResponse response = ReScheduleHearingResponse.builder().ResponseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .reScheduleHearings(scheduledHearings).build();
        log.info("api = /hearing/v1/reschedule/_search, result = SUCCESS");
        return ResponseEntity.accepted().body(response);
    }


    @RequestMapping(value = "/hearing/v1/bulk/_reschedule", method = RequestMethod.POST)
    public ResponseEntity<ReScheduleHearingResponse> bulkRescheduleHearing(@Parameter(in = ParameterIn.DEFAULT, description = "Hearing Details and Request Info", required = true, schema = @Schema()) @Valid @RequestBody BulkReScheduleHearingRequest request) {
        log.info("api =/hearing/v1/bulk/_reschedule, result = IN_PROGRESS");
        List<ReScheduleHearing> scheduledHearings = reScheduleHearingService.bulkReschedule(request);
        ReScheduleHearingResponse response = ReScheduleHearingResponse.builder().ResponseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true))
                .reScheduleHearings(scheduledHearings).build();
        log.info("api =/hearing/v1/bulk/_reschedule, result = SUCCESS");
        return ResponseEntity.accepted().body(response);
    }
}

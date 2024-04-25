package digit.web.controllers;

import digit.service.AsyncSubmissionService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.List;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-29T18:15:41.472193800+05:30[Asia/Calcutta]")
@Controller
@RequestMapping("/submission")
@Slf4j
public class SubmissionApiController{

    private final AsyncSubmissionService asyncSubmissionService;

    @Autowired
    public SubmissionApiController(AsyncSubmissionService asyncSubmissionService) {
        this.asyncSubmissionService = asyncSubmissionService;
    }

    @RequestMapping(value="/v1/duedates", method = RequestMethod.POST)
    public ResponseEntity<AsyncSubmissionResponse> getDueDates(@Parameter(in = ParameterIn.DEFAULT, description = "Give due dates for which files must be submitted and response must be given.", required=true, schema=@Schema()) @Valid @RequestBody AsyncSubmissionRequest submissionRequest) {
        log.info("api = /submission/v1/duedates, result = IN_PROGRESS");
        AsyncSubmission asyncSubmission = asyncSubmissionService.getDueDates(submissionRequest);
        AsyncSubmissionResponse asyncSubmissionResponse = AsyncSubmissionResponse.builder()
                .responseInfo(ResponseInfoFactory.createResponseInfo(submissionRequest.getRequestInfo(), true))
                .asyncSubmissions(Collections.singletonList(asyncSubmission))
                .build();
        log.info("api = /submission/v1/duedates, result = SUCCESS");
        return new ResponseEntity<>(asyncSubmissionResponse, HttpStatus.OK);
    }

    @RequestMapping(value="/v1/getSubmissions", method = RequestMethod.POST)
    public ResponseEntity<AsyncSubmissionResponse> getSubmissions(@Parameter(in = ParameterIn.DEFAULT, description = "Searches for submissions based on criteria given and returns them.", required=true, schema=@Schema()) @Valid @RequestBody AsyncSubmissionSearchRequest searchRequest) {
        log.info("api = /submission/v1/getSubmissions, result = IN_PROGRESS");
        List<AsyncSubmission> asyncSubmissions = asyncSubmissionService.getAsyncSubmissions(searchRequest);
        AsyncSubmissionResponse asyncSubmissionResponse = AsyncSubmissionResponse.builder()
                .responseInfo(ResponseInfoFactory.createResponseInfo(searchRequest.getRequestInfo(), true))
                .asyncSubmissions(asyncSubmissions)
                .build();
        log.info("api = /submission/v1/getSubmission, result = SUCCESS");
        return new ResponseEntity<>(asyncSubmissionResponse, HttpStatus.OK);
    }

    @RequestMapping(value="/v1/saveDates", method = RequestMethod.POST)
    public ResponseEntity<AsyncSubmissionResponse> saveSubmissionDates(@Parameter(in = ParameterIn.DEFAULT, description = "Save dates for which files must be submitted and response must be given.", required=true, schema=@Schema()) @Valid @RequestBody AsyncSubmissionRequest asyncSubmissionRequest) {
        log.info("api = /submission/v1/saveDates, result = IN_PROGRESS");
        AsyncSubmission asyncSubmission = asyncSubmissionService.saveAsyncSubmissions(asyncSubmissionRequest);
        AsyncSubmissionResponse asyncSubmissionResponse = AsyncSubmissionResponse.builder()
                .responseInfo(ResponseInfoFactory.createResponseInfo(asyncSubmissionRequest.getRequestInfo(), true))
                .asyncSubmissions(Collections.singletonList(asyncSubmission))
                .build();
        log.info("api = /submission/v1/saveDates, result = SUCCESS");
        return new ResponseEntity<>(asyncSubmissionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value="/v1/updateDates", method = RequestMethod.POST)
    public ResponseEntity<AsyncSubmissionResponse> updateSubmissionDates(@Parameter(in = ParameterIn.DEFAULT, description = "Update dates for which files must be submitted and response must be given.", required=true, schema=@Schema()) @Valid @RequestBody AsyncSubmissionRequest asyncSubmissionRequest) {
        log.info("api = /submission/v1/updateDates, result = IN_PROGRESS");
        AsyncSubmission asyncSubmission = asyncSubmissionService.updateAsyncSubmissions(asyncSubmissionRequest);
        AsyncSubmissionResponse asyncSubmissionResponse = AsyncSubmissionResponse.builder()
                .responseInfo(ResponseInfoFactory.createResponseInfo(asyncSubmissionRequest.getRequestInfo(), true))
                .asyncSubmissions(Collections.singletonList(asyncSubmission))
                .build();
        log.info("api = /submission/v1/saveDates, result = SUCCESS");
        return new ResponseEntity<>(asyncSubmissionResponse, HttpStatus.OK);
    }

}

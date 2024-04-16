package digit.web.controllers;


import digit.web.models.ErrorRes;
import digit.web.models.JudgeCalendarConfigRequest;
import digit.web.models.JudgeCalendarConfigResponse;
import digit.web.models.JudgeCalendarConfigUpdateRequest;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-15T13:15:39.759211883+05:30[Asia/Kolkata]")
@Controller
@RequestMapping("")
public class JudgeCalendarApiController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    public JudgeCalendarApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @RequestMapping(value = "/judge/v1/calendar/{judgeId}", method = RequestMethod.GET)
    public ResponseEntity<JudgeCalendarConfigResponse> getJudgeCalendar(@Parameter(in = ParameterIn.PATH, description = "the ID of the judge calendar", required = true, schema = @Schema()) @PathVariable("Id") UUID id) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<JudgeCalendarConfigResponse>(objectMapper.readValue("true", JudgeCalendarConfigResponse.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                return new ResponseEntity<JudgeCalendarConfigResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<JudgeCalendarConfigResponse>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/judge/v1/calendar-availability", method = RequestMethod.POST)
    public ResponseEntity<JudgeCalendarConfigResponse> getAvailabilityOfJudge(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the new judge meta data for whom the calendar should be created.", required = true, schema = @Schema()) @Valid @RequestBody JudgeCalendarConfigRequest body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<JudgeCalendarConfigResponse>(objectMapper.readValue("{  \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\",  \"responseInfo\" : {    \"ver\" : \"ver\",    \"resMsgId\" : \"resMsgId\",    \"msgId\" : \"msgId\",    \"apiId\" : \"apiId\",    \"ts\" : 0,    \"status\" : \"SUCCESSFUL\"  },  \"calendarConfig\" : \"calendarConfig\",  \"judgeId\" : \"judgeId\"}", JudgeCalendarConfigResponse.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                return new ResponseEntity<JudgeCalendarConfigResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<JudgeCalendarConfigResponse>(HttpStatus.NOT_IMPLEMENTED);
    }

//    @RequestMapping(value = "/judgeCalendar/v1/create", method = RequestMethod.POST)
//    public ResponseEntity<JudgeCalendarConfigResponse> judgeCalendarV1CreatePost(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the new judge meta data for whom the calendar should be created.", required = true, schema = @Schema()) @Valid @RequestBody JudgeCalendarConfigRequest body) {
//        String accept = request.getHeader("Accept");
//        if (accept != null && accept.contains("application/json")) {
//            try {
//                return new ResponseEntity<JudgeCalendarConfigResponse>(objectMapper.readValue("{  \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\",  \"responseInfo\" : {    \"ver\" : \"ver\",    \"resMsgId\" : \"resMsgId\",    \"msgId\" : \"msgId\",    \"apiId\" : \"apiId\",    \"ts\" : 0,    \"status\" : \"SUCCESSFUL\"  },  \"calendarConfig\" : \"calendarConfig\",  \"judgeId\" : \"judgeId\"}", JudgeCalendarConfigResponse.class), HttpStatus.NOT_IMPLEMENTED);
//            } catch (IOException e) {
//                return new ResponseEntity<JudgeCalendarConfigResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//
//        return new ResponseEntity<JudgeCalendarConfigResponse>(HttpStatus.NOT_IMPLEMENTED);
//    }

    @RequestMapping(value = " /judge/v1/calendar-update", method = RequestMethod.POST)
    public ResponseEntity<JudgeCalendarConfigResponse> updateJudgeCalendar(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the judge calendar data to be updated.", required = true, schema = @Schema()) @Valid @RequestBody JudgeCalendarConfigUpdateRequest body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<JudgeCalendarConfigResponse>(objectMapper.readValue("{  \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\",  \"responseInfo\" : {    \"ver\" : \"ver\",    \"resMsgId\" : \"resMsgId\",    \"msgId\" : \"msgId\",    \"apiId\" : \"apiId\",    \"ts\" : 0,    \"status\" : \"SUCCESSFUL\"  },  \"calendarConfig\" : \"calendarConfig\",  \"judgeId\" : \"judgeId\"}", JudgeCalendarConfigResponse.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                return new ResponseEntity<JudgeCalendarConfigResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<JudgeCalendarConfigResponse>(HttpStatus.NOT_IMPLEMENTED);
    }

}

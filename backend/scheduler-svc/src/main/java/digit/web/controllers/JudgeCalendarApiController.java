package digit.web.controllers;


import digit.service.CalendarService;
import digit.util.ResponseInfoFactory;
import digit.web.models.JudgeAvailabilitySearchRequest;
import digit.web.models.JudgeCalendarResponse;
import digit.web.models.JudgeCalendarUpdateRequest;

import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-15T13:15:39.759211883+05:30[Asia/Kolkata]")
@RestController("judgeCalendarApiController")
@RequestMapping("/judge")
public class JudgeCalendarApiController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;


    private final CalendarService calendarService;

    @Autowired
    public JudgeCalendarApiController(ObjectMapper objectMapper, HttpServletRequest request, CalendarService calendarService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.calendarService = calendarService;
    }


    @RequestMapping(value = "/v1/calendar", method = RequestMethod.POST)
    public ResponseEntity<JudgeCalendarResponse> getJudgeCalendar(@Parameter(in = ParameterIn.DEFAULT, description = "Judge calendar search criteria and Request info", required = true, schema = @Schema()) @Valid @RequestBody JudgeAvailabilitySearchRequest requestBody) {
        //call service here
        calendarService.getJudgeCalendar(requestBody.getCriteria());
        JudgeCalendarResponse response = JudgeCalendarResponse.builder().judgeCalendar(new ArrayList<>()).responseInfo(ResponseInfoFactory.createResponseInfo(requestBody.getRequestInfo(), true)).build();

        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/v1/calendar-availability", method = RequestMethod.POST)
    public ResponseEntity<JudgeCalendarResponse> getAvailabilityOfJudge(@Parameter(in = ParameterIn.DEFAULT, description = "Judge availability search criteria and Request info", required = true, schema = @Schema()) @Valid @RequestBody JudgeAvailabilitySearchRequest requestBody) {


        //call service here


        JudgeCalendarResponse response = JudgeCalendarResponse.builder().judgeCalendar(new ArrayList<>()).responseInfo(ResponseInfoFactory.createResponseInfo(requestBody.getRequestInfo(), true)).build();

        return ResponseEntity.accepted().body(response);

    }


    @RequestMapping(value = " /v1/calendar-update", method = RequestMethod.POST)
    public ResponseEntity<JudgeCalendarResponse> updateJudgeCalendar(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the judge calendar data to be updated.", required = true, schema = @Schema()) @Valid @RequestBody JudgeCalendarUpdateRequest requestBody) {
        JudgeCalendarResponse response = JudgeCalendarResponse.builder().judgeCalendar(new ArrayList<>()).responseInfo(ResponseInfoFactory.createResponseInfo(requestBody.getRequestInfo(), true)).build();

        return ResponseEntity.accepted().body(response);
    }

}

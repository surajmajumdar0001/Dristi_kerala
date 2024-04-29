package digit.web.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import digit.service.CalendarService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-15T13:15:39.759211883+05:30[Asia/Kolkata]")
@RestController("judgeCalendarApiController")
@RequestMapping("")
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


    @RequestMapping(value = "/judge/v1/_calendar", method = RequestMethod.POST)
    public ResponseEntity<JudgeCalendarResponse> getJudgeCalendar(@Parameter(in = ParameterIn.DEFAULT, description = "Judge calendar search criteria and Request info", required = true, schema = @Schema()) @Valid @RequestBody JudgeCalendarSearchRequest request) {
        //service call
        List<HearingCalendar> judgeCalendar = calendarService.getJudgeCalendar(request);

        JudgeCalendarResponse response = JudgeCalendarResponse.builder().calendar(judgeCalendar).responseInfo(ResponseInfoFactory.createResponseInfo(request.getRequestInfo(), true)).build();

        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/judge/v1/_availability", method = RequestMethod.POST)
    public ResponseEntity<List<String>> getAvailabilityOfJudge(@Parameter(in = ParameterIn.DEFAULT, description = "Judge availability search criteria and Request info", required = true, schema = @Schema()) @Valid @RequestBody JudgeAvailabilitySearchRequest request) {

        //service call
        List<String> judgeAvailability = calendarService.getJudgeAvailability(request);

        return ResponseEntity.accepted().body(judgeAvailability);

    }


    @RequestMapping(value = "/judge/v1/_update", method = RequestMethod.POST)
    public ResponseEntity<?> updateJudgeCalendar(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the judge calendar data to be updated.", required = true, schema = @Schema()) @Valid @RequestBody JudgeCalendarUpdateRequest request) {

        //service call
        List<JudgeCalendarRule> updatedJudgeCalendarRule = calendarService.upsert(request);

        return ResponseEntity.accepted().body(updatedJudgeCalendarRule);
    }

}

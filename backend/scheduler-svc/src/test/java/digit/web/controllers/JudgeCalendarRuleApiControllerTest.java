package digit.web.controllers;

import digit.TestConfiguration;
import digit.service.CalendarService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API tests for JudgeCalendarApiController
 */
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(JudgeCalendarApiController.class)
@Import(TestConfiguration.class)
public class JudgeCalendarRuleApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CalendarService calendarService;

    @Mock
    private ResponseInfoFactory responseInfoFactory;

    @InjectMocks
    private JudgeCalendarApiController judgeCalendarApiController;

    @Test
    public void testGetJudgeCalendar() {
        JudgeCalendarSearchRequest request = new JudgeCalendarSearchRequest();
        HearingCalendar hearingCalendar = new HearingCalendar();
        List<HearingCalendar> judgeCalendar = List.of(hearingCalendar);
        RequestInfo requestInfo = new RequestInfo();
        request.setRequestInfo(requestInfo);

        when(calendarService.getJudgeCalendar(request)).thenReturn(judgeCalendar);
        when(responseInfoFactory.createResponseInfo(requestInfo, true)).thenReturn(new ResponseInfo());

        ResponseEntity<JudgeCalendarResponse> judgeCalendarResponse = judgeCalendarApiController.getJudgeCalendar(request);

        assertEquals(HttpStatus.OK, judgeCalendarResponse.getStatusCode());
        assertEquals(judgeCalendarResponse.getBody().getCalendar(), judgeCalendar);

    }

    @Test
    public void getAvailabilityOfJudgeTest(){
        JudgeAvailabilitySearchRequest request = new JudgeAvailabilitySearchRequest();
        AvailabilityDTO availability = new AvailabilityDTO();
        List<AvailabilityDTO> judgeAvailability = List.of(availability);
        RequestInfo requestInfo = new RequestInfo();
        request.setRequestInfo(requestInfo);
        when(calendarService.getJudgeAvailability(request)).thenReturn(judgeAvailability);
        when(responseInfoFactory.createResponseInfo(requestInfo, true)).thenReturn(new ResponseInfo());
        ResponseEntity<JudgeAvailabilityResponse> judgeAvailabilityResponse = judgeCalendarApiController.getAvailabilityOfJudge(request);

        assertEquals(HttpStatus.OK, judgeAvailabilityResponse.getStatusCode());
        assertEquals(judgeAvailabilityResponse.getBody().getAvailableDates(), judgeAvailability);
    }

    @Test
    public void updateJudgeCalendarTest(){
        JudgeCalendarUpdateRequest request = new JudgeCalendarUpdateRequest();
        JudgeCalendarRule judgeCalendarRule = new JudgeCalendarRule();
        List<JudgeCalendarRule> updatedJudgeCalendarRule = List.of(judgeCalendarRule);

        when(calendarService.upsert(request)).thenReturn(updatedJudgeCalendarRule);

        ResponseEntity<?> updatedJudgeCalendarRuleResponse = judgeCalendarApiController.updateJudgeCalendar(request);

        assertEquals(HttpStatus.OK, updatedJudgeCalendarRuleResponse.getStatusCode());
        assertEquals(updatedJudgeCalendarRuleResponse.getBody(), updatedJudgeCalendarRule);
    }

    @Test
    public void getIsHearingValidSuccess() throws Exception {
        mockMvc.perform(post("/judgeCalendarRule/v1/exists/{Id}").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void getIsHearingValidFailure() throws Exception {
        mockMvc.perform(post("/judgeCalendarRule/v1/exists/{Id}").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void judgeCalendarV1AvailabilityPostSuccess() throws Exception {
        mockMvc.perform(post("/judgeCalendarRule/v1/availability").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void judgeCalendarV1AvailabilityPostFailure() throws Exception {
        mockMvc.perform(post("/judgeCalendarRule/v1/availability").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void judgeCalendarV1CreatePostSuccess() throws Exception {
        mockMvc.perform(post("/judgeCalendarRule/v1/create").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void judgeCalendarV1CreatePostFailure() throws Exception {
        mockMvc.perform(post("/judgeCalendarRule/v1/create").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void judgeCalendarV1UpdatePostSuccess() throws Exception {
        mockMvc.perform(post("/judgeCalendarRule/v1/update").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void judgeCalendarV1UpdatePostFailure() throws Exception {
        mockMvc.perform(post("/judgeCalendarRule/v1/update").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

}

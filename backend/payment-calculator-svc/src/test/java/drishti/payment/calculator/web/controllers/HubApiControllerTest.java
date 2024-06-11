package drishti.payment.calculator.web.controllers;

import drishti.payment.calculator.web.models.ErrorResponse;
import drishti.payment.calculator.web.models.HubSearchRequest;
import drishti.payment.calculator.web.models.PostalHubRequest;
import drishti.payment.calculator.web.models.PostalHubResponse;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import drishti.payment.calculator.TestConfiguration;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* API tests for HubApiController
*/
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(HubApiController.class)
@Import(TestConfiguration.class)
public class HubApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createHubSuccess() throws Exception {
        mockMvc.perform(post("/hub/v1/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

    @Test
    public void createHubFailure() throws Exception {
        mockMvc.perform(post("/hub/v1/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

    @Test
    public void searchHubSuccess() throws Exception {
        mockMvc.perform(post("/hub/v1/_search").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

    @Test
    public void searchHubFailure() throws Exception {
        mockMvc.perform(post("/hub/v1/_search").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

    @Test
    public void updateHubSuccess() throws Exception {
        mockMvc.perform(post("/hub/v1/_update").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

    @Test
    public void updateHubFailure() throws Exception {
        mockMvc.perform(post("/hub/v1/_update").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

}

package drishti.payment.calculator.web.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import drishti.payment.calculator.web.models.HubSearchRequest;
import drishti.payment.calculator.web.models.PostalHubRequest;
import drishti.payment.calculator.web.models.PostalHubResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-06-10T14:05:42.847785340+05:30[Asia/Kolkata]")
@Controller
@RequestMapping("")
public class HubApiController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    public HubApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @RequestMapping(value = "/hub/v1/_create", method = RequestMethod.POST)
    public ResponseEntity<PostalHubResponse> createHub(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody PostalHubRequest body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<PostalHubResponse>(objectMapper.readValue("{ }", PostalHubResponse.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                return new ResponseEntity<PostalHubResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<PostalHubResponse>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/hub/v1/_search", method = RequestMethod.POST)
    public ResponseEntity<PostalHubResponse> searchHub(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody HubSearchRequest body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<PostalHubResponse>(objectMapper.readValue("{ }", PostalHubResponse.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                return new ResponseEntity<PostalHubResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<PostalHubResponse>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/hub/v1/_update", method = RequestMethod.POST)
    public ResponseEntity<PostalHubResponse> updateHub(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody PostalHubRequest body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<PostalHubResponse>(objectMapper.readValue("{ }", PostalHubResponse.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                return new ResponseEntity<PostalHubResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<PostalHubResponse>(HttpStatus.NOT_IMPLEMENTED);
    }

}

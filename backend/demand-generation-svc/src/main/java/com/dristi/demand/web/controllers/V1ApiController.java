package com.dristi.demand.web.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-07-19T16:56:31.228060503+05:30[Asia/Kolkata]")
@RestController
@RequestMapping("")
public class V1ApiController {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    public V1ApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @RequestMapping(value = "/v1/_intercept", method = RequestMethod.POST)
    public ResponseEntity<Object> v1InterceptPost(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @RequestParam(value = "eSignResponse", required = true) String eSignResponse, @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @RequestParam(value = "espTxnID", required = true) String espTxnID) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Object>(objectMapper.readValue("{ }", Object.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
    }

}

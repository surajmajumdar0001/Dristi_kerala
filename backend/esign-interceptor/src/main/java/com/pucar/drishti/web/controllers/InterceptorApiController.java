package com.pucar.drishti.web.controllers;


import com.pucar.drishti.config.Configuration;
import com.pucar.drishti.service.InterceptorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-07-02T12:37:46.343081666+05:30[Asia/Kolkata]")
@RestController
@RequestMapping("")
public class InterceptorApiController {

    private final InterceptorService service;
    private final Configuration configs;

    @Autowired
    public InterceptorApiController(InterceptorService service, Configuration configs) {
        this.service = service;
        this.configs = configs;
    }


    @RequestMapping(value = "/v1/_intercept", method = RequestMethod.POST)
    public ResponseEntity<?> eSignV1Interceptor(@RequestParam("eSignResponse") String response, @RequestParam("espTxnID") String espId) {
        String filestoreId = service.process(response, espId);

        // fixme : send filestore id in custom header, discuss with suresh first
        HttpHeaders headers = new HttpHeaders();
        String redirectUri = configs.getRedirectUrl();
        headers.setLocation(URI.create(redirectUri));
        return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);

    }

}

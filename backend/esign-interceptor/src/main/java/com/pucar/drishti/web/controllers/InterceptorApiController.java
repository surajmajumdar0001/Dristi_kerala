package com.pucar.drishti.web.controllers;


import com.pucar.drishti.config.Configuration;
import com.pucar.drishti.service.InterceptorService;
import jakarta.annotation.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;


@Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-07-02T12:37:46.343081666+05:30[Asia/Kolkata]")
@RestController
@RequestMapping("")
@Slf4j
public class InterceptorApiController {

    private final InterceptorService service;
    private final Configuration configs;

    @Autowired
    public InterceptorApiController(InterceptorService service, Configuration configs) {
        this.service = service;
        this.configs = configs;
    }

    @RequestMapping(value = "/v1/redirect", method = RequestMethod.GET)
    public ResponseEntity<?> redirectHandler(@RequestParam("result") String result, @RequestParam("filestoreId") String filestoreId, @RequestParam("userType") String userType) {
        log.info("redirecting through get method");

        // Construct the final redirect URL
        String redirectUri = configs.getRedirectUrl() + userType + "/dristi";
        redirectUri += "?result=" + result + "&filestoreId=" + filestoreId;

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUri));
        log.info("redirectUri {}", redirectUri);
        return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
    }


    @RequestMapping(value = "/v1/_intercept", method = RequestMethod.POST)
    public ModelAndView eSignV1Interceptor(@RequestParam("eSignResponse") String response, @RequestParam("espTxnID") String espId) {

        log.info(response);
        log.info(espId);


        String filestoreId = "";
        String result = "error";

        int firstHyphenIndex = espId.indexOf("-");
        int secondHyphenIndex = espId.indexOf("-", firstHyphenIndex + 1);
        log.info("calculating tenantId,pageModule,fileStore id");
        String tenantId = espId.substring(0, firstHyphenIndex);
        String pageModule = espId.substring(firstHyphenIndex + 1, secondHyphenIndex);
        String fileStoreId = espId.substring(secondHyphenIndex + 1);
        log.info("tenantId {} ,pageModule {} , fileStoreId {}", tenantId, pageModule, fileStoreId);
        try {
            log.info("sending response to sign doc");
            filestoreId = service.process(response, espId, tenantId, fileStoreId);
            result = "success";
            log.info("successfully sign doc");
        } catch (Exception e) {
            log.error("Error Occured While signing the doc");

        }

        log.info("generating uri to redirect");

        String userType;
        if (pageModule.equals("en")) {
            userType = "employee";
        } else if (pageModule.equals("ci")) {
            userType = "citizen";
        } else {
            throw new RuntimeException("Invalid pageModule: " + pageModule);
        }

        // Redirect to the GET handler with parameters
        ModelAndView modelAndView = new ModelAndView("redirect:/v1/redirect");
        modelAndView.addObject("result", result);
        modelAndView.addObject("filestoreId", filestoreId);
        modelAndView.addObject("userType", userType);

        return modelAndView;

    }

}

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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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


    @RequestMapping(value = "/v1/_intercept", method = RequestMethod.POST)
    public ResponseEntity<?> eSignV1Interceptor(@RequestParam("eSignResponse") String response, @RequestParam("espTxnID") String espId) {
        String filestoreId = "";
        String result = "error";

        int firstHyphenIndex = espId.indexOf("-");
        int secondHyphenIndex = espId.indexOf("-", firstHyphenIndex + 1);

        String tenantId = espId.substring(0, firstHyphenIndex);
        String pageModule = espId.substring(firstHyphenIndex + 1, secondHyphenIndex);
        String fileStoreId = espId.substring(secondHyphenIndex + 1);

        try {

            filestoreId = service.process(response, espId, tenantId, fileStoreId);
            result = "success";
        } catch (Exception e) {
            log.error("Error Occured While signing the doc");

        }

        String userType;
        if (pageModule.equals("en")) {
            userType = "employee";
        } else if (pageModule.equals("ci")) {
            userType = "citizen";
        } else {
            throw new RuntimeException("Invalid pageModule: " + pageModule);
        }
        // fixme : send filestore id in custom header, discuss with suresh first
        HttpHeaders headers = new HttpHeaders();
        String redirectUri = configs.getRedirectUrl();
        redirectUri = redirectUri + userType + "/dristi";

        HashMap<String, String> map = new HashMap<>();
        map.put("result", result);
        map.put("filestoreId", filestoreId);

        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        redirectUri += "?" + params;
        redirectUri = redirectUri.endsWith("&") ? redirectUri.substring(0, redirectUri.length() - 1) : redirectUri;


//        HttpHeaders headers = new HttpHeaders();
//        String redirectUri = configs.getRedirectUrl();
//        redirectUri = redirectUri + userType + "/dristi?result=" + result + "filestoreId=" + filestoreId;

        headers.setLocation(URI.create(redirectUri));
        return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);

    }

}

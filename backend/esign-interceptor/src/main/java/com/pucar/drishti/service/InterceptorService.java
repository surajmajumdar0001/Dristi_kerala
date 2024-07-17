package com.pucar.drishti.service;


import com.pucar.drishti.config.Configuration;
import com.pucar.drishti.repository.ServiceRequestRepository;
import com.pucar.drishti.util.FileStoreUtil;
import com.pucar.drishti.web.models.SignDocParameter;
import com.pucar.drishti.web.models.SignDocRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.LinkedHashMap;

@Service
@Slf4j
public class InterceptorService {

    private final FileStoreUtil util;
    private final ServiceRequestRepository restCall;
    private final Configuration configs;

    @Autowired
    public InterceptorService(FileStoreUtil util, ServiceRequestRepository restCall, Configuration configs) {
        this.util = util;
        this.restCall = restCall;
        this.configs = configs;
    }

    public String process(String response, String espId, String tenantId, String fileStoreId) {
        log.info("generating token for created user");
        log.info(espId);
        String token = oAuthForDristi();
        log.info ("validating by calling filestore id");
        util.fetchFileStoreObjectById(fileStoreId, tenantId); // validation of transaction
        SignDocRequest request = getSignDocRequest(token, response, fileStoreId, tenantId);

        StringBuilder uri = new StringBuilder();
        uri.append(configs.getESignHost()).append(configs.getESignEndPoint());

        Object result = restCall.callESign(uri, request);

        log.info("signed fileStore id {} :", result.toString());

        return result.toString();

    }

    private SignDocRequest getSignDocRequest(String token, String response, String fileStoreId, String tenantId) {

        RequestInfo requestInfo = RequestInfo.builder().authToken(token).build();  //fixme: update user for this

        SignDocParameter parameter = SignDocParameter.builder()
                .fileStoreId(fileStoreId).response(response).tenantId(tenantId).build();

        return SignDocRequest.builder().requestInfo(requestInfo).eSignParameter(parameter).build();
    }

    private String oAuthForDristi() {

        StringBuilder uri = new StringBuilder("https://dristi-kerala-dev.pucar.org/user/oauth/token?_=1713357247536");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "esigninterceptor");
        map.add("password", "Beehyv@123");
        map.add("tenantId", "kl");
        map.add("userType", "EMPLOYEE");
        map.add("scope", "read");
        map.add("grant_type", "password");


        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-cache");
        headers.setConnection("keep-alive");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDo=");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        Object response = restCall.fetchResult(uri, request);
        return ((LinkedHashMap) response).get("access_token").toString();
    }
}

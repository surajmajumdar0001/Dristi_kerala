package com.pucar.drishti.service;


import com.pucar.drishti.config.Configuration;
import com.pucar.drishti.repository.ServiceRequestRepository;
import com.pucar.drishti.util.FileStoreUtil;
import com.pucar.drishti.web.models.SignDocParameter;
import com.pucar.drishti.web.models.SignDocRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public String process(String response, String espId) {

        String tenantId = espId.substring(0, 2); ///fixme: length might be change check other way
        String fileStoreId = espId.substring(2);   /// length might be change check other way
        util.fetchFileStoreObjectById(fileStoreId, tenantId); // validation of transaction
        SignDocRequest request = getSignDocRequest(response, fileStoreId, tenantId);

        StringBuilder uri = new StringBuilder();
        uri.append(configs.getESignHost()).append(configs.getESignEndPoint());
        Object result = restCall.fetchResult(uri, request);

        log.info("signed fileStore id {} :", result.toString());

        return result.toString();

    }

    private SignDocRequest getSignDocRequest(String response, String fileStoreId, String tenantId) {

        RequestInfo requestInfo = RequestInfo.builder().build();  //fixme: update user for this

        SignDocParameter parameter = SignDocParameter.builder()
                .fileStoreId(fileStoreId).response(response).tenantId(tenantId).build();

        SignDocRequest request = SignDocRequest.builder().requestInfo(requestInfo)
                .eSignParameter(parameter).build();
        return request;
    }
}

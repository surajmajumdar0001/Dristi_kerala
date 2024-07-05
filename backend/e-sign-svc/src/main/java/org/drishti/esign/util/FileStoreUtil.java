package org.drishti.esign.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.drishti.esign.config.Configuration;
import org.drishti.esign.repository.ServiceRequestRepository;
import org.drishti.esign.web.models.StorageResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FileStoreUtil {


    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final Configuration configs;
    private final ServiceRequestRepository serviceRepository;

    @Autowired
    public FileStoreUtil(RestTemplate restTemplate, ObjectMapper mapper, Configuration configs, ServiceRequestRepository serviceRepository) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.configs = configs;
        this.serviceRepository = serviceRepository;
    }


    public Resource fetchFileStoreObjectById(String fileStoreId, String tenantId) {

        StringBuilder uri = new StringBuilder();
        uri.append(configs.getFilestoreHost()).append(configs.getFilestoreSearchEndPoint());
        uri = appendQueryParams(uri, "fileStoreId", fileStoreId, "tenantId", tenantId);
        Resource object;
        try {
            object = restTemplate.getForObject(uri.toString(), Resource.class);
            return object;

        } catch (Exception e) {
            throw new CustomException("FILESTORE_SERVICE_EXCEPTION", "exception occurred while calling filestore service");

        }


    }

    public StringBuilder appendQueryParams(StringBuilder uri, String paramName1, String paramValue1, String paramName2, String paramValue2) {
        if (uri.indexOf("?") == -1) {
            uri.append("?");
        } else {
            uri.append("&");
        }
        uri.append(paramName1).append("=").append(paramValue1).append("&");
        uri.append(paramName2).append("=").append(paramValue2);
        return uri;
    }


    public String storeFileInFileStore(MultipartFile file, String tenantId) {
        String module = "signed";  // fixme: take me from constant file
        StringBuilder uri = new StringBuilder();
        uri.append(configs.getFilestoreHost()).append(configs.getFilestoreCreateEndPoint());

        List<MultipartFile> files = new ArrayList<>();
        files.add(file);

        Map<String, Object> request = new HashMap<>();
        request.put("file", files);
        request.put("tenantId", tenantId);
        request.put("module", module);

        Object response = serviceRepository.fetchResult(uri, request);
        StorageResponse storageResponse = mapper.convertValue(response, StorageResponse.class);


        return storageResponse.getFiles().get(0).getFileStoreId(); //fixme: handle null wala part


    }


}

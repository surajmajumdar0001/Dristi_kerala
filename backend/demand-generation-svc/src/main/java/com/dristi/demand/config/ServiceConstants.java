package com.dristi.demand.config;


import org.springframework.stereotype.Component;


@Component
public class ServiceConstants {

    public static final String EXTERNAL_SERVICE_EXCEPTION = "External Service threw an Exception: ";
    public static final String SEARCHER_SERVICE_EXCEPTION = "Exception while fetching from searcher: ";
    public static final String ERROR_WHILE_FETCHING_FROM_MDMS = "Exception occurred while fetching category lists from mdms: ";

    public static final String MDMS_RESPONSE_JSONPATH = "$.MdmsRes.DEMAND.DemandConfiguration[?(@.updateTopic==\'{{MODULE_NAME}}\')]";
    public static final String MODULE_PLACEHOLDER = "{{MODULE_NAME}}";
    public static final String DEMAND_CONFIG_NAME = "DemandConfiguration";
    public static final String DEMAND_MODULE_CODE = "DEMAND";

}

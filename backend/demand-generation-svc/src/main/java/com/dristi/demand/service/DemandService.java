package com.dristi.demand.service;

import com.dristi.demand.config.Configuration;
import com.dristi.demand.repository.ServiceRequestRepository;
import com.dristi.demand.web.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DemandService {

    private final Configuration config;

    private final ObjectMapper mapper;

    private final ServiceRequestRepository repository;

    @Autowired
    public DemandService(Configuration config, ObjectMapper mapper, ServiceRequestRepository repository) {
        this.config = config;
        this.mapper = mapper;
        this.repository = repository;
    }

    public List<Demand> generateDemands(RequestInfo requestInfo, List<Calculation> calculations , String moduleCode, String taxHeadMasterCode) {
        List<Demand> demands = new ArrayList<>();

        for (Calculation calculation : calculations) {
            DemandDetail demandDetail = DemandDetail.builder()
                    .tenantId(calculation.getTenantId())
                    .taxAmount(BigDecimal.valueOf(calculation.getTotalAmount()))
                    .taxHeadMasterCode(taxHeadMasterCode).build();

            Demand demand = Demand.builder()
                    .tenantId(calculation.getTenantId()).consumerCode(calculation.getApplicationId())
                    .consumerType("PAYMENT_BND_CONSUMER_CODE")
                    .businessService(moduleCode)
                    .taxPeriodFrom(System.currentTimeMillis()).taxPeriodTo(System.currentTimeMillis())
                    .demandDetails(Collections.singletonList(demandDetail))
                    .build();

            demands.add(demand);
        }

        StringBuilder url = new StringBuilder().append(config.getBillingServiceHost())
                .append(config.getDemandCreateEndpoint());

        DemandRequest demandRequest = DemandRequest.builder().requestInfo(requestInfo).demands(demands).build();

        Object response = repository.fetchResult(url, demandRequest);

        DemandResponse demandResponse = mapper.convertValue(response, DemandResponse.class);
        return demandResponse.getDemands();
    }



}

package org.pucar.dristi.service;

import org.pucar.dristi.config.EPostConfiguration;
import org.pucar.dristi.kafka.Producer;
import org.pucar.dristi.model.*;
import org.pucar.dristi.repository.EPostRepository;
import org.pucar.dristi.util.EpostUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EPostService {


    private final EPostConfiguration configuration;

    private final EPostRepository ePostRepository;

    private final EpostUtil epostUtil;

    private final Producer producer;

    @Autowired
    public EPostService(EPostConfiguration configuration, EPostRepository ePostRepository, EpostUtil epostUtil, Producer producer) {
        this.configuration = configuration;
        this.ePostRepository = ePostRepository;
        this.epostUtil = epostUtil;
        this.producer = producer;
    }

    public ChannelMessage sendEPost(TaskRequest request) {

        EPostTracker ePostTracker = epostUtil.createPostTrackerBody(request);

        EPostRequest ePostRequest = EPostRequest.builder().requestInfo(request.getRequestInfo()).ePostTracker(ePostTracker).build();
        producer.push("save-epost-tracker", ePostRequest);

        return ChannelMessage.builder().processNumber(ePostTracker.getProcessNumber()).build();
    }

    public EPostResponse getEPost(EPostTrackerSearchRequest searchRequest) {
        return ePostRepository.getEPostTrackerResponse(searchRequest.getEPostTrackerSearchCriteria());
    }

    public EPostTracker updateEPost(EPostRequest ePostRequest) {

        EPostTracker ePostTracker = epostUtil.updateEPostTracker(ePostRequest);

        EPostRequest postRequest = EPostRequest.builder().requestInfo(ePostRequest.getRequestInfo()).ePostTracker(ePostTracker).build();
        producer.push("update-epost-tracker",postRequest);

        return ePostTracker;
    }
}
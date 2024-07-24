package org.pucar.dristi.service;

import org.pucar.dristi.config.EPostConfiguration;
import org.pucar.dristi.kafka.Producer;
import org.pucar.dristi.model.ChannelMessage;
import org.pucar.dristi.model.EPostTracker;
import org.pucar.dristi.model.EPostTrackerSearchRequest;
import org.pucar.dristi.model.TaskRequest;
import org.pucar.dristi.repository.EPostRepository;
import org.pucar.dristi.util.EpostUtil;
import org.pucar.dristi.util.IdgenUtil;
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

        //TODO - should send E Post Tracker + Request Info
        producer.push("save-epost-tracker", ePostTracker);

        // TODO - generate channel message from processNo and return it
        return null;
    }

    public List<EPostTracker> getEPost(EPostTrackerSearchRequest searchRequest, Integer limit, Integer offset) {
        return ePostRepository.getEPost(searchRequest.getEPostTrackerSearchCriteria(), limit, offset);
    }

    public EPostTracker updateEPost(EPostTracker ePostTracker) {
        return null;
    }
}

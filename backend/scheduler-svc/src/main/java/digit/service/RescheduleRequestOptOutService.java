package digit.service;


import digit.config.Configuration;
import digit.enrichment.RescheduleRequestOptOutEnrichment;
import digit.kafka.Producer;
import digit.repository.RescheduleRequestOptOutRepository;
import digit.validator.RescheduleRequestOptOutValidator;
import digit.web.models.OptOut;
import digit.web.models.OptOutRequest;
import digit.web.models.OptOutSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RescheduleRequestOptOutService {

    @Autowired
    private RescheduleRequestOptOutRepository rescheduleRequestOptOutRepository;

    @Autowired
    private RescheduleRequestOptOutValidator rescheduleRequestOptOutValidator;
    @Autowired
    private RescheduleRequestOptOutEnrichment rescheduleRequestOptOutEnrichment;

    @Autowired
    private Producer producer;

    @Autowired
    private Configuration config;

    @Autowired
    private WorkflowService workflowService;


    public List<OptOut> create(OptOutRequest request) {

        rescheduleRequestOptOutValidator.validateRequest(request);

        rescheduleRequestOptOutEnrichment.enrichCreateRequest(request);

        producer.push(config.getOptOutTopic(), request.getOptOuts());

        producer.push("check-opt-out", request);

        return request.getOptOuts();
    }

    public List<OptOut> update(OptOutRequest request) {

        rescheduleRequestOptOutValidator.validateUpdateRequest(request);

        rescheduleRequestOptOutEnrichment.enrichUpdateRequest(request);

        producer.push(config.getOptOutUpdateTopic(), request.getOptOuts());

        return request.getOptOuts();
    }

    public List<OptOut> search(OptOutSearchRequest request) {
        return rescheduleRequestOptOutRepository.getOptOut(request.getCriteria());
    }
}

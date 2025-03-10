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

    private final RescheduleRequestOptOutRepository optOutRepository;

    private final RescheduleRequestOptOutValidator optOutValidator;

    private final RescheduleRequestOptOutEnrichment optOutEnrichment;

    private final Producer producer;

    private final Configuration config;

    @Autowired
    public RescheduleRequestOptOutService(RescheduleRequestOptOutRepository optOutRepository, RescheduleRequestOptOutValidator optOutValidator, RescheduleRequestOptOutEnrichment optOutEnrichment, Producer producer, Configuration config) {
        this.optOutRepository = optOutRepository;
        this.optOutValidator = optOutValidator;
        this.optOutEnrichment = optOutEnrichment;
        this.producer = producer;
        this.config = config;
    }

    /**
     * @param request
     * @return
     */
    public List<OptOut> create(OptOutRequest request) {
        log.info("operation = create, result = IN_PROGRESS, OptOut = {}", request.getOptOuts());

        optOutValidator.validateRequest(request);

        optOutEnrichment.enrichCreateRequest(request);

        producer.push(config.getOptOutTopic(), request.getOptOuts());

        producer.push("check-opt-out", request);

        log.info("operation = create, result = SUCCESS, OptOut = {}", request.getOptOuts());

        return request.getOptOuts();
    }

    /**
     * @param request
     * @return
     */
    public List<OptOut> update(OptOutRequest request) {
        log.info("operation = update, result = IN_PROGRESS, OptOut = {}", request.getOptOuts());

        optOutValidator.validateUpdateRequest(request);

        optOutEnrichment.enrichUpdateRequest(request);

        producer.push(config.getOptOutUpdateTopic(), request.getOptOuts());

        log.info("operation = update, result = SUCCESS, OptOut = {}", request.getOptOuts());

        return request.getOptOuts();
    }

    /**
     * @param request
     * @return
     */

    public List<OptOut> search(OptOutSearchRequest request, Integer limit, Integer offset) {
        return optOutRepository.getOptOut(request.getCriteria(), limit, offset);
    }
}

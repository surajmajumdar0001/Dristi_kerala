package digit.service;


import digit.config.Configuration;
import digit.enrichment.OptOutEnrichment;
import digit.kafka.Producer;
import digit.repository.OptOutRepository;
import digit.validator.OptOutValidator;
import digit.web.models.OptOut;
import digit.web.models.OptOutRequest;
import digit.web.models.OptOutSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OptOutService {

    @Autowired
    private OptOutRepository optOutRepository;

    @Autowired
    private OptOutValidator optOutValidator;
    @Autowired
    private OptOutEnrichment optOutEnrichment;

    @Autowired
    private Producer producer;

    @Autowired
    private Configuration config;


    public List<OptOut> create(OptOutRequest request) {

        optOutValidator.validateRequest(request);

        optOutEnrichment.enrichCreateRequest(request);

        producer.push(config.getOptOutTopic(), request.getOptOuts());

        return request.getOptOuts();
    }

    public List<OptOut> update(OptOutRequest request) {

        optOutValidator.validateUpdateRequest(request);

        optOutEnrichment.enrichUpdateRequest(request);

        producer.push(config.getOptOutUpdateTopic(), request.getOptOuts());

        return request.getOptOuts();
    }

    public List<OptOut> search(OptOutSearchRequest request) {
        return optOutRepository.getOptOut(request.getCriteria());
    }
}

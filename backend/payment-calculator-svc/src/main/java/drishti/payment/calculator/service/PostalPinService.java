package drishti.payment.calculator.service;


import drishti.payment.calculator.config.Configuration;
import drishti.payment.calculator.enrichment.PostalServiceEnrichment;
import drishti.payment.calculator.kafka.Producer;
import drishti.payment.calculator.repository.PostalServiceRepository;
import drishti.payment.calculator.validator.PostalServiceValidator;
import drishti.payment.calculator.web.models.PostalService;
import drishti.payment.calculator.web.models.PostalServiceRequest;
import drishti.payment.calculator.web.models.PostalServiceSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PostalPinService {

    private final PostalServiceRepository repository;
    private final PostalServiceValidator validator;
    private final PostalServiceEnrichment enrichment;
    private final Producer producer;
    private final Configuration config;

    public PostalPinService(PostalServiceRepository repository, PostalServiceValidator validator, PostalServiceEnrichment enrichment, Producer producer, Configuration config) {
        this.repository = repository;
        this.validator = validator;
        this.enrichment = enrichment;
        this.producer = producer;
        this.config = config;
    }

    public List<PostalService> create(PostalServiceRequest request) {

        validator.validatePostalServiceRequest(request);

        enrichment.enrichPostalServiceRequest(request);

        producer.push(config.getPostalServiceCreateTopic(), request.getPostalServices());

        return request.getPostalServices();
    }

    public List<PostalService> search(PostalServiceSearchRequest searchRequest) {
        return repository.getPostalService(searchRequest.getCriteria(), null, null);
    }

    public List<PostalService> update(PostalServiceRequest request) {

        validator.validateExistingPostalServiceRequest(request);

        enrichment.enrichExistingPostalServiceRequest(request);

        producer.push(config.getPostalServiceUpdateTopic(), request.getPostalServices());

        return request.getPostalServices();
    }
}

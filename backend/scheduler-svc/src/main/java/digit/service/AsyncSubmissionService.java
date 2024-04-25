package digit.service;

import digit.config.Configuration;
import digit.enrichment.AsyncSubmissionEnrichment;
import digit.kafka.Producer;
import digit.repository.AsyncSubmissionRepository;
import digit.validator.AsyncSubmissionValidator;
import digit.web.models.AsyncSubmission;
import digit.web.models.AsyncSubmissionRequest;
import digit.web.models.AsyncSubmissionSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class AsyncSubmissionService {

    private AsyncSubmissionRepository repository;

    private Producer producer;

    private Configuration config;

    private WorkflowService workflowService;

    private AsyncSubmissionEnrichment enrichment;

    private AsyncSubmissionValidator validator;

    @Autowired
    public AsyncSubmissionService(AsyncSubmissionRepository repository, Producer producer,
                                  Configuration config, WorkflowService workflowService,
                                  AsyncSubmissionEnrichment enrichment, AsyncSubmissionValidator validator) {
        this.repository = repository;
        this.producer = producer;
        this.config = config;
        this.workflowService = workflowService;
        this.enrichment = enrichment;
        this.validator = validator;
    }


    public AsyncSubmission getDueDates(AsyncSubmissionRequest submissionRequest) {
        log.info("operation = getDueDates, result = IN_PROGRESS");
        AsyncSubmission asyncSubmission = submissionRequest.getAsyncSubmission();
        asyncSubmission.setSubmissionType(LocalDate.now().plusDays(config.getMinAsyncSubmissionDays()).toString());
        asyncSubmission.setSubmissionType(LocalDate.now().plusDays(config.getMinAsyncResponseDays()).toString());
        log.info("operation = getDueDates, result = SUCCESS");
        return asyncSubmission;
    }

    public List<AsyncSubmission> getAsyncSubmissions(AsyncSubmissionSearchRequest searchRequest) {
        log.info("operation = getAsyncSubmissions, with searchRequest : {}", searchRequest.toString());
        return repository.getAsyncSubmissions(searchRequest.getAsyncSubmissionSearchCriteria());
    }

    public AsyncSubmission saveAsyncSubmissions(AsyncSubmissionRequest request) {
        log.info("operation = saveAsyncSubmissions, result = IN_PROGRESS");
        AsyncSubmission asyncSubmission = request.getAsyncSubmission();
        enrichment.enrichAsyncSubmissions(request.getRequestInfo(), asyncSubmission);
        validator.validateHearing(asyncSubmission);
        producer.push(config.getAsyncSubmissionSaveTopic(), AsyncSubmissionRequest.builder().asyncSubmission(asyncSubmission)
                .requestInfo(request.getRequestInfo()));
        log.info("operation = saveAsyncSubmissions, result = SUCCESS");
        return asyncSubmission;
    }

    public AsyncSubmission updateAsyncSubmissions(AsyncSubmissionRequest request) {
        log.info("operation = updateAsyncSubmissions, result = IN_PROGRESS");
        AsyncSubmission asyncSubmission = request.getAsyncSubmission();
        enrichment.enrichUpdateAsyncSubmission(request.getRequestInfo(), asyncSubmission);
        validator.validateHearing(asyncSubmission);
        producer.push(config.getAsyncSubmissionUpdateTopic(), AsyncSubmissionRequest.builder().asyncSubmission(asyncSubmission)
                .requestInfo(request.getRequestInfo()));
        log.info("operation = updateAsyncSubmissions, result = SUCCESS");
        return asyncSubmission;
    }
}

package digit.service;

import digit.config.Configuration;
import digit.enrichment.AsyncSubmissionEnrichment;
import digit.kafka.Producer;
import digit.repository.AsyncSubmissionRepository;
import digit.util.ResponseInfoFactory;
import digit.validator.AsyncSubmissionValidator;
import digit.web.models.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AsyncSubmissionServiceTest {

    @InjectMocks
    private AsyncSubmissionService service;

    @Mock
    private AsyncSubmissionRepository repository;

    @Mock
    private Producer producer;

    @Mock
    private Configuration config;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AsyncSubmissionEnrichment enrichment;

    @Mock
    private AsyncSubmissionValidator validator;

    private AsyncSubmissionRequest submissionRequest;
    private AsyncSubmission asyncSubmission;
    private AsyncSubmissionResponse asyncSubmissionResponse;
    private RequestInfo requestInfo;

    @BeforeEach
    public void setup() {
        asyncSubmission = new AsyncSubmission();
        requestInfo = new RequestInfo();
        submissionRequest = new AsyncSubmissionRequest();
        submissionRequest.setAsyncSubmission(asyncSubmission);
        submissionRequest.setRequestInfo(requestInfo);

        asyncSubmissionResponse = AsyncSubmissionResponse.builder()
                .responseInfo(ResponseInfoFactory.createResponseInfo(requestInfo, true))
                .asyncSubmissions(Collections.singletonList(asyncSubmission))
                .build();

        Mockito.lenient().when(config.getMinAsyncSubmissionDays()).thenReturn(1);
        Mockito.lenient().when(config.getMinAsyncResponseDays()).thenReturn(1);
        Mockito.lenient().when(config.getAsyncSubmissionSaveTopic()).thenReturn("SAVE_TOPIC");
        Mockito.lenient().when(config.getAsyncSubmissionUpdateTopic()).thenReturn("UPDATE_TOPIC");
    }

    @Test
    public void testGetDueDates() {
        AsyncSubmission result = service.getDueDates(submissionRequest);

        assertNotNull(result);
        assertEquals(LocalDate.now().plusDays(1).toString(), result.getSubmissionDate());
        assertEquals(LocalDate.now().plusDays(1).toString(), result.getResponseDate());
        verify(validator, times(1)).validateDates(asyncSubmission);
    }

    @Test
    public void testGetAsyncSubmissions() {
        AsyncSubmissionSearchRequest searchRequest = new AsyncSubmissionSearchRequest();
        AsyncSubmissionSearchCriteria criteria = new AsyncSubmissionSearchCriteria();
        searchRequest.setAsyncSubmissionSearchCriteria(criteria);

        when(repository.getAsyncSubmissions(criteria)).thenReturn(Collections.singletonList(asyncSubmission));

        List<AsyncSubmission> result = service.getAsyncSubmissions(searchRequest);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(asyncSubmission, result.get(0));
    }

    @Test
    public void testSaveAsyncSubmissions() {
        AsyncSubmissionResponse result = service.saveAsyncSubmissions(submissionRequest);

        assertNotNull(result);
        assertEquals(1, result.getAsyncSubmissions().size());
        assertEquals(asyncSubmission, result.getAsyncSubmissions().get(0));
        verify(validator, times(1)).validateSubmissionDates(asyncSubmission);
        verify(enrichment, times(1)).enrichAsyncSubmissions(requestInfo, asyncSubmission);
        verify(producer, times(1)).push("SAVE_TOPIC", asyncSubmissionResponse);
    }

    @Test
    public void testUpdateAsyncSubmissions_Success() {
        asyncSubmission.setSubmissionId("123");

        when(repository.getAsyncSubmissions(any(AsyncSubmissionSearchCriteria.class)))
                .thenReturn(Collections.singletonList(asyncSubmission));

        AsyncSubmissionResponse result = service.updateAsyncSubmissions(submissionRequest);

        assertNotNull(result);
        assertEquals(1, result.getAsyncSubmissions().size());
        assertEquals(asyncSubmission, result.getAsyncSubmissions().get(0));
        verify(validator, times(1)).validateSubmissionDates(asyncSubmission);
        verify(enrichment, times(1)).enrichUpdateAsyncSubmission(requestInfo, asyncSubmission);
        verify(producer, times(1)).push("UPDATE_TOPIC", asyncSubmissionResponse);
    }

    @Test
    public void testUpdateAsyncSubmissions_Failure_InvalidId() {
        assertThrows(CustomException.class, () -> service.updateAsyncSubmissions(submissionRequest));
    }

    @Test
    public void testUpdateAsyncSubmissions_Failure_EmptySubmission() {
        asyncSubmission.setSubmissionId("123");

        when(repository.getAsyncSubmissions(any(AsyncSubmissionSearchCriteria.class)))
                .thenReturn(Collections.emptyList());

        assertThrows(CustomException.class, () -> service.updateAsyncSubmissions(submissionRequest));
    }
}


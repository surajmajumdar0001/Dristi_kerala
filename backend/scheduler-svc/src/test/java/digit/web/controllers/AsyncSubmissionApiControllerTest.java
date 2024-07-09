package digit.web.controllers;

import digit.service.AsyncSubmissionService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AsyncSubmissionApiControllerTest {

    @Mock
    private AsyncSubmissionService asyncSubmissionService;

    @Mock
    private ResponseInfoFactory responseInfoFactory;

    @InjectMocks
    private AsyncSubmissionApiController asyncSubmissionApiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDueDates() {
        AsyncSubmissionRequest request = new AsyncSubmissionRequest();
        AsyncSubmission asyncSubmission = new AsyncSubmission();
        when(asyncSubmissionService.getDueDates(any(AsyncSubmissionRequest.class))).thenReturn(asyncSubmission);

        ResponseEntity<AsyncSubmissionResponse> responseEntity = asyncSubmissionApiController.getDueDates(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().getAsyncSubmissions().size());
        assertEquals(asyncSubmission, responseEntity.getBody().getAsyncSubmissions().get(0));
    }

    @Test
    void testGetSubmissions() {
        AsyncSubmissionSearchRequest request = new AsyncSubmissionSearchRequest();
        List<AsyncSubmission> asyncSubmissions = Collections.singletonList(new AsyncSubmission());
        when(asyncSubmissionService.getAsyncSubmissions(any(AsyncSubmissionSearchRequest.class))).thenReturn(asyncSubmissions);

        ResponseEntity<AsyncSubmissionResponse> responseEntity = asyncSubmissionApiController.getSubmissions(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().getAsyncSubmissions().size());
        assertEquals(asyncSubmissions.get(0), responseEntity.getBody().getAsyncSubmissions().get(0));
    }

    @Test
    void testSaveSubmissionDates() {
        AsyncSubmissionRequest request = new AsyncSubmissionRequest();
        AsyncSubmissionResponse response = new AsyncSubmissionResponse();
        when(asyncSubmissionService.saveAsyncSubmissions(any(AsyncSubmissionRequest.class))).thenReturn(response);

        ResponseEntity<AsyncSubmissionResponse> responseEntity = asyncSubmissionApiController.saveSubmissionDates(request);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }

    @Test
    void testUpdateSubmissionDates() {
        AsyncSubmissionRequest request = new AsyncSubmissionRequest();
        AsyncSubmissionResponse response = new AsyncSubmissionResponse();
        when(asyncSubmissionService.updateAsyncSubmissions(any(AsyncSubmissionRequest.class))).thenReturn(response);

        ResponseEntity<AsyncSubmissionResponse> responseEntity = asyncSubmissionApiController.updateSubmissionDates(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(response, responseEntity.getBody());
    }
}

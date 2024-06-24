package digit.service;

import digit.config.Configuration;
import digit.enrichment.RescheduleRequestOptOutEnrichment;
import digit.kafka.Producer;
import digit.repository.RescheduleRequestOptOutRepository;
import digit.validator.RescheduleRequestOptOutValidator;
import digit.web.models.OptOut;
import digit.web.models.OptOutRequest;
import digit.web.models.OptOutSearchCriteria;
import digit.web.models.OptOutSearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RescheduleRequestOptOutServiceTest {

    @Mock
    private RescheduleRequestOptOutRepository optOutRepository;

    @Mock
    private RescheduleRequestOptOutValidator optOutValidator;

    @Mock
    private RescheduleRequestOptOutEnrichment optOutEnrichment;

    @Mock
    private Producer producer;

    @Mock
    private Configuration config;

    @InjectMocks
    private RescheduleRequestOptOutService optOutService;

    @Test
    void testCreate() {
        OptOutRequest request = new OptOutRequest();
        OptOut optOut = new OptOut();
        request.setOptOuts(Arrays.asList(optOut));
        when(config.getOptOutTopic()).thenReturn("opt-out-topic");

        List<OptOut> result = optOutService.create(request);

        verify(optOutValidator).validateRequest(request);
        verify(optOutEnrichment).enrichCreateRequest(request);
        verify(producer).push("opt-out-topic", request.getOptOuts());
        verify(producer).push("check-opt-out", request);
        assertEquals(request.getOptOuts(), result);
    }

    @Test
    void testUpdate() {
        OptOutRequest request = new OptOutRequest();
        OptOut optOut = new OptOut();
        request.setOptOuts(Arrays.asList(optOut));
        when(config.getOptOutUpdateTopic()).thenReturn("opt-out-update-topic");

        List<OptOut> result = optOutService.update(request);

        verify(optOutValidator).validateUpdateRequest(request);
        verify(optOutEnrichment).enrichUpdateRequest(request);
        verify(producer).push("opt-out-update-topic", request.getOptOuts());
        assertEquals(request.getOptOuts(), result);
    }

    @Test
    void testSearch() {
        OptOutSearchRequest request = new OptOutSearchRequest();
        OptOutSearchCriteria criteria = new OptOutSearchCriteria();
        request.setCriteria(criteria);
        List<OptOut> expectedOptOuts = Arrays.asList(new OptOut());
        when(optOutRepository.getOptOut(criteria, 10, 0)).thenReturn(expectedOptOuts);

        List<OptOut> result = optOutService.search(request, 10, 0);

        assertEquals(expectedOptOuts, result);
    }
}

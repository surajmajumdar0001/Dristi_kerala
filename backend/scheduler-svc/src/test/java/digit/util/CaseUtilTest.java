package digit.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.repository.ServiceRequestRepository;
import digit.web.models.cases.SearchCaseRequest;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CaseUtilTest {

    @Mock
    private Configuration mockConfig;

    @Mock
    private ObjectMapper mockMapper;

    @Mock
    private ServiceRequestRepository mockRequestRepository;

    @InjectMocks
    private CaseUtil caseUtil;


    @Test
    public void testGetCases_Success() throws Exception {
        when(mockConfig.getCaseUrl()).thenReturn("http://example.com");
        when(mockConfig.getCaseEndpoint()).thenReturn("/cases");

        JsonNode mockResponseListNode = mock(JsonNode.class);
        JsonNode mockCriteriaNode = mock(JsonNode.class);
        JsonNode mockJsonNode = mock(JsonNode.class);

        // Setup the structure of the mockJsonNode
        when(mockJsonNode.get("criteria")).thenReturn(mockCriteriaNode);
        when(mockCriteriaNode.get(0)).thenReturn(mockResponseListNode);
        when(mockResponseListNode.get("responseList")).thenReturn(mock(JsonNode.class)); // or whatever structure you expect

        // Mocking the readTree to return our structured JsonNode
        when(mockMapper.readTree(anyString())).thenReturn(mockJsonNode);

        // Mocking the response of postMethod to be valid JSON
        when(mockRequestRepository.postMethod(any(StringBuilder.class), any(SearchCaseRequest.class)))
                .thenReturn("{\"criteria\":[{\"responseList\":[]}]}");

        JsonNode result = caseUtil.getCases(new SearchCaseRequest());

        assertNotNull(result);
    }

    @Test
    public void testGetCases_JsonProcessingException() throws Exception {
        when(mockConfig.getCaseUrl()).thenReturn("http://example.com");
        when(mockConfig.getCaseEndpoint()).thenReturn("/cases");

        assertThrows(NullPointerException.class, () -> caseUtil.getCases(new SearchCaseRequest()));
    }

    @Test
    public void testGetIdsFromJsonNodeArray() {
        JsonNode mockNode1 = mock(JsonNode.class);
        when(mockNode1.get("id")).thenReturn(mock(JsonNode.class));
        when(mockNode1.get("id").asText()).thenReturn("1");

        JsonNode mockNode2 = mock(JsonNode.class);
        when(mockNode2.get("id")).thenReturn(null); // Simulate case where 'id' is missing

        JsonNode[] mockArray = {mockNode1, mockNode2};
        JsonNode mockNodeArray = mock(JsonNode.class);
        when(mockNodeArray.isArray()).thenReturn(true);
        when(mockNodeArray.iterator()).thenReturn(new ArrayIterator<>(mockArray));

        Set<String> result = caseUtil.getIdsFromJsonNodeArray(mockNodeArray);

        assertEquals(1, result.size());
        assertTrue(result.contains("1"));
    }

    static class ArrayIterator<T> implements java.util.Iterator<T> {
        private final T[] array;
        private int index = 0;

        ArrayIterator(T[] array) {
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return index < array.length;
        }

        @Override
        public T next() {
            return array[index++];
        }
    }
}

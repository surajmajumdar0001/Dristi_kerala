package drishti.payment.calculator.repository.querybuilder;

import drishti.payment.calculator.web.models.HubSearchCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PostalHubQueryBuilderTest {

    @InjectMocks
    private PostalHubQueryBuilder postalHubQueryBuilder;

    @Test
    public void testGetPostalHubQuery() {
        HubSearchCriteria criteria = new HubSearchCriteria();
        Integer limit = 10;
        Integer offset = 0;
        List<Object> preparedStmtList = new ArrayList<>();

        String query = postalHubQueryBuilder.getPostalHubQuery(criteria, preparedStmtList, limit, offset);

        assertNotNull(query);
        assertTrue(query.contains("SELECT *  FROM postal_hub ph"));
    }


}

package drishti.payment.calculator.repository.querybuilder;


import drishti.payment.calculator.web.models.PostalServiceSearchCriteria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostalServiceQueryBuilder {

    private static final String FROM_TABLES = " FROM  ";
    private final String BASE_APPLICATION_QUERY = "SELECT  ";
    private final String ORDER_BY = " ORDER BY ";
    private final String GROUP_BY = " GROUP BY ";
    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";

    public String getPostalServiceQuery(PostalServiceSearchCriteria criteria, List<Object> preparedStmtList, Integer limit, Integer offset) {
        return BASE_APPLICATION_QUERY + FROM_TABLES;
    }
}

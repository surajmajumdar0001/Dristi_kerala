package drishti.payment.calculator.repository.querybuilder;


import org.springframework.stereotype.Component;

@Component
public class PostalServiceQueryBuilder {

    private static final String FROM_TABLES = " FROM  ";
    private final String BASE_APPLICATION_QUERY = "SELECT  ";
    private final String ORDER_BY = " ORDER BY ";
    private final String GROUP_BY = " GROUP BY ";
    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";
}

package drishti.payment.calculator.repository;


import drishti.payment.calculator.repository.querybuilder.PostalServiceQueryBuilder;
import drishti.payment.calculator.repository.rowmapper.PostalServiceRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PostalServiceRepository {

    private final PostalServiceRowMapper rowMapper;
    private final PostalServiceQueryBuilder queryBuilder;

    @Autowired
    public PostalServiceRepository(PostalServiceRowMapper rowMapper, PostalServiceQueryBuilder queryBuilder) {
        this.rowMapper = rowMapper;
        this.queryBuilder = queryBuilder;
    }



}

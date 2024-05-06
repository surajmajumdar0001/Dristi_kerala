package digit.repository;


import digit.repository.querybuilder.OptOutQueryBuilder;
import digit.repository.rowmapper.OptOutRowMapper;
import digit.web.models.HearingSearchCriteria;
import digit.web.models.OptOut;
import digit.web.models.OptOutSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class OptOutRepository {

    @Autowired
    private OptOutRowMapper optOutRowMapper;

    @Autowired
    private OptOutQueryBuilder optOutQueryBuilder;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<OptOut> getOptOut(OptOutSearchCriteria optOutSearchCriteria) {

        List<Object> preparedStmtList = new ArrayList<>();
        String query = optOutQueryBuilder.getOptOutQuery(optOutSearchCriteria, preparedStmtList);
        log.debug("Final query: " + query);
        return jdbcTemplate.query(query, preparedStmtList.toArray(),optOutRowMapper);


    }
}

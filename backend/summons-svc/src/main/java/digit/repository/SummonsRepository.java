package digit.repository;

import digit.repository.querybuilder.SummonsQueryBuilder;
import digit.repository.rowmapper.SummonsRowMapper;
import digit.web.models.Summons;
import digit.web.models.SummonsSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class SummonsRepository {

    private final JdbcTemplate jdbcTemplate;

    private final SummonsQueryBuilder queryBuilder;

    private final SummonsRowMapper rowMapper;

    public SummonsRepository(JdbcTemplate jdbcTemplate, SummonsQueryBuilder queryBuilder, SummonsRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
    }


    public List<Summons> getSummons(SummonsSearchCriteria searchCriteria) {
        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getSummonsQuery(searchCriteria, preparedStmtList);
        log.debug("Final query: " + query);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }
}

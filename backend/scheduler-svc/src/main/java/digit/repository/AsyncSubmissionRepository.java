package digit.repository;

import digit.repository.querybuilder.AsyncSubmissionQueryBuilder;
import digit.repository.rowmapper.AsyncSubmissionRowMapper;
import digit.web.models.AsyncSubmission;
import digit.web.models.AsyncSubmissionSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class AsyncSubmissionRepository {

    private AsyncSubmissionQueryBuilder queryBuilder;

    private AsyncSubmissionRowMapper rowMapper;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public AsyncSubmissionRepository(AsyncSubmissionQueryBuilder queryBuilder, AsyncSubmissionRowMapper rowMapper, JdbcTemplate jdbcTemplate) {
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AsyncSubmission> getAsyncSubmissions(AsyncSubmissionSearchCriteria searchCriteria) {

        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getAsyncSubmissionQuery(searchCriteria, preparedStmtList);
        log.debug("Final query: " + query);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }
}

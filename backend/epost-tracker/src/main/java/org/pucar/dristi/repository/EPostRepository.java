package org.pucar.dristi.repository;

import lombok.extern.slf4j.Slf4j;
import org.pucar.dristi.model.EPostTracker;
import org.pucar.dristi.model.EPostTrackerSearchCriteria;
import org.pucar.dristi.model.TaskRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class EPostRepository {

    private final JdbcTemplate jdbcTemplate;
    private final EPostQueryBuilder ePostQueryBuilder;
    private final EPostRowMapper ePostRowMapper;

    public EPostRepository(JdbcTemplate jdbcTemplate, EPostQueryBuilder ePostQueryBuilder, EPostRowMapper ePostRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.ePostQueryBuilder = ePostQueryBuilder;
        this.ePostRowMapper = ePostRowMapper;
    }
    public void sendEPost(TaskRequest body,String processNumber){

    }
    public List<EPostTracker> getEPost(EPostTrackerSearchCriteria searchCriteria){
        List<String> preparedStmtList = new ArrayList<>();
        String query = ePostQueryBuilder.getEPostTracker(searchCriteria, preparedStmtList);
        log.debug("Final query: " + query);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), ePostRowMapper);
    }
}

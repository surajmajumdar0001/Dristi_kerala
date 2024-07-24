package org.pucar.dristi.repository;

import lombok.extern.slf4j.Slf4j;
import org.pucar.dristi.model.TaskRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}

package digit.repository;


import digit.repository.querybuilder.HearingQueryBuilder;
import digit.repository.rowmapper.HearingRowMapper;
import digit.web.models.HearingSearchCriteria;
import digit.web.models.ScheduleHearing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class HearingRepository {
    @Autowired
    private HearingQueryBuilder queryBuilder;

    @Autowired
    private HearingRowMapper rowMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ScheduleHearing> getHearings(HearingSearchCriteria hearingSearchCriteria) {

        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getHearingQuery(hearingSearchCriteria, preparedStmtList);
        log.debug("Final query: " + query);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);

    }

    public List<String> getAvailableDatesOfJudges(HearingSearchCriteria hearingSearchCriteria) {

        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getJudgeAvailableDatesQuery(hearingSearchCriteria, preparedStmtList);
        log.debug("Final query: " + query);
        return jdbcTemplate.queryForList(query, preparedStmtList.toArray(), String.class);


    }


}

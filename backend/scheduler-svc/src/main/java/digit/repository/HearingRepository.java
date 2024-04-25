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

    public List<ScheduleHearing> getJudgeHearing(HearingSearchCriteria hearingSearchCriteria) {

        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getJudgeHearingQuery(hearingSearchCriteria, preparedStmtList);
        log.debug("Final query: " + query);
        return jdbcTemplate.query(query, rowMapper);

    }

    public List<String> getAvailableDatesOfJudges(HearingSearchCriteria hearingSearchCriteria) {

        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getJudgeAvailableDatesQuery(hearingSearchCriteria, preparedStmtList);
        log.debug("Final query: " + query);
        return jdbcTemplate.queryForList(query, String.class);


    }


}

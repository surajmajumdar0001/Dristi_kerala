package digit.repository.rowmapper;

import digit.web.models.CauseList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

@Component
@Slf4j
public class CauseListRowMapper implements RowMapper<CauseList> {

    @Override
    public CauseList mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return CauseList.builder()
                .courtId(resultSet.getString("court_id"))
                .caseId(resultSet.getString("case_id"))
                .tenantId(resultSet.getString("tenant_id"))
                .judgeId(resultSet.getString("judge_id"))
                .typeOfHearing(resultSet.getString("hearing_type"))
                .litigantNames(Arrays.asList(resultSet.getString("litigant_names").split(",")))
                .tentativeSlot(resultSet.getString("tentative_slot"))
                .caseTitle(resultSet.getString("case_title"))
                .caseDate(resultSet.getString("case_date"))
                .build();
    }
}

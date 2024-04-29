package digit.repository.rowmapper;

import digit.models.coremodels.AuditDetails;
import digit.web.models.JudgeCalendarRule;
import digit.web.models.enums.JudgeRuleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


@Component
@Slf4j
public class CalendarRowMapper implements RowMapper<JudgeCalendarRule> {
    @Override
    public JudgeCalendarRule mapRow(ResultSet resultSet, int rowNum) throws SQLException {


        JudgeCalendarRule calendar = JudgeCalendarRule.builder()
                .id(resultSet.getString("id"))
                .judgeId(resultSet.getString("judgeId"))
                .ruleType(JudgeRuleType.valueOf(resultSet.getString("ruleType")))
                .date(LocalDate.parse(resultSet.getString("date")))
                .notes(resultSet.getString("notes"))
                .tenantId(resultSet.getString("tenantId"))
                .auditDetails(AuditDetails.builder()
                        .createdBy(resultSet.getString("createdby"))
                        .createdTime(resultSet.getLong("createdtime"))
                        .lastModifiedBy(resultSet.getString("lastmodifiedby"))
                        .lastModifiedTime(resultSet.getLong("lastmodifiedtime"))
                        .build())
                .rowVersion(resultSet.getInt("rowVersion"))
                .build();

        return calendar;
    }
}

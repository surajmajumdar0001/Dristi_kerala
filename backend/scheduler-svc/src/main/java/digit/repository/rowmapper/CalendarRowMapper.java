package digit.repository.rowmapper;

import digit.web.models.JudgeCalendarRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Component
@Slf4j
public class CalendarRowMapper implements RowMapper<JudgeCalendarRule> {
    @Override
    public JudgeCalendarRule mapRow(ResultSet resultSet, int rowNum) throws SQLException {


        JudgeCalendarRule calendar = JudgeCalendarRule.builder()
                .id(resultSet.getString("id"))
                .date(LocalDate.parse(resultSet.getString("date")))
                .notes(resultSet.getString("notes"))
                .judgeId(resultSet.getString("judgeId"))
                .ruleType(resultSet.getString("ruleType")).build();

        return calendar;
    }
}

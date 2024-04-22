package digit.repository.rowmapper;

import digit.web.models.JudgeCalendar;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;


@Component
@Slf4j
public class CalendarRowMapper implements RowMapper<JudgeCalendar> {
    @Override
    public JudgeCalendar mapRow(ResultSet resultSet, int rowNum) throws SQLException {


        JudgeCalendar calendar = JudgeCalendar.builder()
                .id(resultSet.getString("id"))
                .date(LocalDateTime.parse(resultSet.getString("date")))
                .notes(resultSet.getString("notes"))
                .judgeId(resultSet.getString("judgeId"))
                .ruleType(resultSet.getString("ruleType")).build();

        return calendar;
    }
}

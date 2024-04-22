package digit.repository.rowmapper;

import digit.web.models.ScheduleHearing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class HearingRowMapper implements RowMapper<ScheduleHearing> {
    @Override
    public ScheduleHearing mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        ScheduleHearing hearing = ScheduleHearing.builder()
                .date(resultSet.getString("date"))
                .description(resultSet.getString("description"))
                .hearingBookingId(resultSet.getString("hearingBookingId"))
                .judgeId(resultSet.getString(""))
                .build();
        return null;
    }
}

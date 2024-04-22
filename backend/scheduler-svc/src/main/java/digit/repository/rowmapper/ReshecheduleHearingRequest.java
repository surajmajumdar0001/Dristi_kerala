package digit.repository.rowmapper;

import digit.web.models.ReScheduleHearing;
import digit.web.models.ScheduleHearing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
@Slf4j
public class ReshecheduleHearingRequest implements RowMapper<ReScheduleHearing> {

    @Override
    public ReScheduleHearing mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        ReScheduleHearing reScheduleHearing = ReScheduleHearing.builder()
                .requesterId(resultSet.getString("requesterId"))
                .hearingBookingId(resultSet.getString("hearingBookingId"))
                .actionComment(resultSet.getString("actionComment"))
                .reason(resultSet.getString("reason"))
                .rescheduledRequestId(resultSet.getString("rescheduleRequestId"))
                .status(resultSet.getString("status"))
                .build();
        return reScheduleHearing;
    }
}

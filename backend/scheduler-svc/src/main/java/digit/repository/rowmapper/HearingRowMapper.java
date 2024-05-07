package digit.repository.rowmapper;

import digit.models.coremodels.AuditDetails;
import digit.web.models.ScheduleHearing;
import digit.web.models.enums.EventType;
import digit.web.models.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class HearingRowMapper implements RowMapper<ScheduleHearing> {
    @Override
    public ScheduleHearing mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        String pattern = "yyyy-MM-dd HH:mm:ss";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        ScheduleHearing hearing = ScheduleHearing.builder()
                .date(LocalDate.parse(resultSet.getString("hearing_date")))
                .description(resultSet.getString("description"))
                .hearingBookingId(resultSet.getString("hearing_booking_id"))
                .tenantId(resultSet.getString("tenant_id"))
                .courtId(resultSet.getString("court_id"))
                .judgeId(resultSet.getString("judge_id"))
                .caseId(resultSet.getString("case_id"))
                .eventType(resultSet.getString("event_type")==null?null:EventType.valueOf(resultSet.getString("event_type")))
                .title(resultSet.getString("title"))
                .status(resultSet.getString("status")==null?null:Status.valueOf(resultSet.getString("status")))
                .startTime(LocalDateTime.parse(resultSet.getString("start_time"),formatter))
                .endTime(LocalDateTime.parse(resultSet.getString("end_time"),formatter))
                .auditDetails(AuditDetails.builder()
                        .createdBy(resultSet.getString("created_by"))
                        .createdTime(resultSet.getLong("created_time"))
                        .lastModifiedBy(resultSet.getString("last_modified_by"))
                        .lastModifiedTime(resultSet.getLong("last_modified_time"))
                        .build())
                .rowVersion(resultSet.getInt("row_version")).build();
        return hearing;
    }
}

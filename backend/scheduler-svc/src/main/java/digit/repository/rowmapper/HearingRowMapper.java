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

@Component
@Slf4j
public class HearingRowMapper implements RowMapper<ScheduleHearing> {
    @Override
    public ScheduleHearing mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        ScheduleHearing hearing = ScheduleHearing.builder()
                .date(LocalDate.parse(resultSet.getString("date")))
                .description(resultSet.getString("description"))
                .hearingBookingId(resultSet.getString("hearingBookingId"))
                .tenantId(resultSet.getString("tenantId"))
                .courtId(resultSet.getString("courtId"))
                .judgeId(resultSet.getString("judgeId"))
                .caseId(resultSet.getString("caseId"))
                .eventType(EventType.valueOf(resultSet.getString("eventType")))
                .title(resultSet.getString("title"))
                .status(Status.valueOf(resultSet.getString("status")))
                .startTime(resultSet.getTimestamp("startTime").toLocalDateTime())
                .endTime(resultSet.getTimestamp("endTime").toLocalDateTime())
                .auditDetails(AuditDetails.builder()
                        .createdBy(resultSet.getString("createdby"))
                        .createdTime(resultSet.getLong("createdtime"))
                        .lastModifiedBy(resultSet.getString("lastmodifiedby"))
                        .lastModifiedTime(resultSet.getLong("lastmodifiedtime"))
                        .build())
                .rowVersion(resultSet.getInt("rowVersion")).build();
        return hearing;
    }
}

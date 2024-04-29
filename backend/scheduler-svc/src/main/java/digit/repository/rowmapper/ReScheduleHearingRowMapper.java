package digit.repository.rowmapper;

import digit.models.coremodels.AuditDetails;
import digit.web.models.ReScheduleHearing;
import digit.web.models.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
@Slf4j
public class ReScheduleHearingRowMapper implements RowMapper<ReScheduleHearing> {

    @Override
    public ReScheduleHearing mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        ReScheduleHearing reScheduleHearing = ReScheduleHearing.builder()
                .rescheduledRequestId(resultSet.getString("rescheduledRequestId"))
                .hearingBookingId(resultSet.getString("hearingBookingId"))
                .tenantId(resultSet.getString("tenantId"))
                .judgeId(resultSet.getString("judgeId"))
                .caseId(resultSet.getString("caseId"))
                .requesterId(resultSet.getString("requesterId"))
                .reason(resultSet.getString("reason"))
                .status(Status.valueOf(resultSet.getString("status")))
                .actionComment(resultSet.getString("actionComment"))

                .auditDetails(AuditDetails.builder()
                        .createdBy(resultSet.getString("auditDetails.createdBy"))
                        .createdTime(resultSet.getLong("auditDetails.createdTime"))
                        .lastModifiedBy(resultSet.getString("auditDetails.lastModifiedBy"))
                        .lastModifiedTime(resultSet.getLong("auditDetails.lastModifiedTime"))
                        .build())
                .rowVersion(resultSet.getLong("rowVersion"))
                .build();
        return reScheduleHearing;
    }
}

package digit.repository.rowmapper;

import digit.web.models.AsyncSubmission;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class AsyncSubmissionRowMapper implements RowMapper<AsyncSubmission> {

    @Override
    public AsyncSubmission mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return AsyncSubmission.builder()
                .submissionId(resultSet.getString("async_submission_id"))
                .courtId(resultSet.getString("court_id"))
                .caseId(resultSet.getString("case_id"))
                .judgeId(resultSet.getString("judge_id"))
                .submissionType(resultSet.getString("submission_type"))
                .title(resultSet.getString("title"))
                .status(resultSet.getString("status"))
                .description(resultSet.getString("description"))
                .auditDetails(AuditDetails.builder()
                        .createdBy(resultSet.getString("created_by"))
                        .createdTime(resultSet.getLong("created_time"))
                        .lastModifiedBy(resultSet.getString("last_modified_by"))
                        .lastModifiedTime(resultSet.getLong("last_modified_time"))
                        .build())
                .rowVersion(resultSet.getInt("row_version"))
                .tenantId(resultSet.getString("tenant_id"))
                .build();
    }
}

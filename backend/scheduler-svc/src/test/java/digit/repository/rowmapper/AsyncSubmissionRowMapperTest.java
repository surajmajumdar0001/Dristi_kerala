package digit.repository.rowmapper;

import digit.web.models.AsyncSubmission;
import org.egov.common.contract.models.AuditDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AsyncSubmissionRowMapperTest {

    @Mock
    private RowMapper<AsyncSubmission> mapper;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private AsyncSubmissionRowMapper rowMapper;

    @Test
    void testMapRow() throws SQLException {
        // Setup mock ResultSet
        when(resultSet.getString("async_submission_id")).thenReturn("submission-id-1");
        when(resultSet.getString("court_id")).thenReturn("court-id-1");
        when(resultSet.getString("case_id")).thenReturn("case-id-1");
        when(resultSet.getString("judge_id")).thenReturn("judge-id-1");
        when(resultSet.getString("submission_type")).thenReturn("type-1");
        when(resultSet.getString("title")).thenReturn("Title 1");
        when(resultSet.getString("status")).thenReturn("status-1");
        when(resultSet.getString("description")).thenReturn("description-1");
        when(resultSet.getString("submission_date")).thenReturn("2024-07-01");
        when(resultSet.getString("response_date")).thenReturn("2024-07-02");
        when(resultSet.getString("created_by")).thenReturn("user-1");
        when(resultSet.getLong("created_time")).thenReturn(1625239073000L);
        when(resultSet.getString("last_modified_by")).thenReturn("user-2");
        when(resultSet.getLong("last_modified_time")).thenReturn(1625239074000L);
        when(resultSet.getInt("row_version")).thenReturn(1);
        when(resultSet.getString("tenant_id")).thenReturn("tenant-id-1");

        // Map row
        AsyncSubmission submission = rowMapper.mapRow(resultSet, 1);

        // Assertions
        assertEquals("submission-id-1", submission.getSubmissionId());
        assertEquals("court-id-1", submission.getCourtId());
        assertEquals("case-id-1", submission.getCaseId());
        assertEquals("judge-id-1", submission.getJudgeId());
        assertEquals("type-1", submission.getSubmissionType());
        assertEquals("Title 1", submission.getTitle());
        assertEquals("status-1", submission.getStatus());
        assertEquals("description-1", submission.getDescription());
        assertEquals("2024-07-01", submission.getSubmissionDate());
        assertEquals("2024-07-02", submission.getResponseDate());

        AuditDetails auditDetails = submission.getAuditDetails();
        assertEquals("user-1", auditDetails.getCreatedBy());
        assertEquals(1625239073000L, auditDetails.getCreatedTime());
        assertEquals("user-2", auditDetails.getLastModifiedBy());
        assertEquals(1625239074000L, auditDetails.getLastModifiedTime());

        assertEquals(1, submission.getRowVersion());
        assertEquals("tenant-id-1", submission.getTenantId());
    }

    @Test
    void testMapRowWithNullValues() throws SQLException {
        // Setup mock ResultSet with null values
        when(resultSet.getString("async_submission_id")).thenReturn(null);
        when(resultSet.getString("court_id")).thenReturn(null);
        when(resultSet.getString("case_id")).thenReturn(null);
        when(resultSet.getString("judge_id")).thenReturn(null);
        when(resultSet.getString("submission_type")).thenReturn(null);
        when(resultSet.getString("title")).thenReturn(null);
        when(resultSet.getString("status")).thenReturn(null);
        when(resultSet.getString("description")).thenReturn(null);
        when(resultSet.getString("submission_date")).thenReturn(null);
        when(resultSet.getString("response_date")).thenReturn(null);
        when(resultSet.getString("created_by")).thenReturn(null);
        when(resultSet.getLong("created_time")).thenReturn(0L);
        when(resultSet.getString("last_modified_by")).thenReturn(null);
        when(resultSet.getLong("last_modified_time")).thenReturn(0L);
        when(resultSet.getInt("row_version")).thenReturn(0);
        when(resultSet.getString("tenant_id")).thenReturn(null);

        // Map row
        AsyncSubmission submission = rowMapper.mapRow(resultSet, 1);

        // Assertions
        assertEquals(null, submission.getSubmissionId());
        assertEquals(null, submission.getCourtId());
        assertEquals(null, submission.getCaseId());
        assertEquals(null, submission.getJudgeId());
        assertEquals(null, submission.getSubmissionType());
        assertEquals(null, submission.getTitle());
        assertEquals(null, submission.getStatus());
        assertEquals(null, submission.getDescription());
        assertEquals(null, submission.getSubmissionDate());
        assertEquals(null, submission.getResponseDate());

        AuditDetails auditDetails = submission.getAuditDetails();
        assertEquals(null, auditDetails.getCreatedBy());
        assertEquals(0L, auditDetails.getCreatedTime());
        assertEquals(null, auditDetails.getLastModifiedBy());
        assertEquals(0L, auditDetails.getLastModifiedTime());

        assertEquals(0, submission.getRowVersion());
        assertEquals(null, submission.getTenantId());
    }
}


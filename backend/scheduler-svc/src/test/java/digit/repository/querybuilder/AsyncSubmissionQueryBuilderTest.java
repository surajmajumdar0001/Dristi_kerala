package digit.repository.querybuilder;

import digit.web.models.AsyncSubmissionSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AsyncSubmissionQueryBuilderTest {

    @InjectMocks
    private AsyncSubmissionQueryBuilder queryBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAsyncSubmissionQuery_AllFields() {
        AsyncSubmissionSearchCriteria criteria = new AsyncSubmissionSearchCriteria();
        criteria.setSubmissionIds(Arrays.asList("sub1", "sub2"));
        criteria.setCourtId("court1");
        criteria.setJudgeIds(Arrays.asList("judge1", "judge2"));
        criteria.setCaseIds(Arrays.asList("case1", "case2"));
        criteria.setSubmissionDate(LocalDate.of(2023, 1, 1));
        criteria.setResponseDate(LocalDate.of(2023, 1, 2));

        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getAsyncSubmissionQuery(criteria, preparedStmtList);

        String expectedQuery = "SELECT asb.court_id, asb.case_id, asb.judge_id, asb.async_submission_id, asb.submission_type, asb.title, asb.description, asb.status, asb.submission_date, asb.response_date, asb.created_by, asb.created_time, asb.last_modified_by, asb.last_modified_time, asb.row_version, asb.tenant_id " +
                "FROM async_submission asb WHERE asb.async_submission_id IN ( ?,? ) AND asb.court_id = ? AND asb.judge_id IN ( ?,? ) AND asb.case_id IN ( ?,? ) AND asb.submission_date = ? AND asb.response_date = ? ORDER BY asb.case_id, asb.submission_type";


        assertNotNull(query);
        assertEquals(9, preparedStmtList.size());
        assertEquals("sub1", preparedStmtList.get(0));
        assertEquals("sub2", preparedStmtList.get(1));
        assertEquals("court1", preparedStmtList.get(2));
        assertEquals("judge1", preparedStmtList.get(3));
        assertEquals("judge2", preparedStmtList.get(4));
        assertEquals("case1", preparedStmtList.get(5));
        assertEquals("case2", preparedStmtList.get(6));
        assertEquals("2023-01-01", preparedStmtList.get(7));
        assertEquals("2023-01-02", preparedStmtList.get(8));
    }

    @Test
    void testGetAsyncSubmissionQuery_EmptyCriteria() {
        AsyncSubmissionSearchCriteria criteria = new AsyncSubmissionSearchCriteria();

        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getAsyncSubmissionQuery(criteria, preparedStmtList);

        assertNotNull(query);
        assertEquals(0, preparedStmtList.size());
    }

    @Test
    void testGetAsyncSubmissionQuery_SubmissionIdsOnly() {
        AsyncSubmissionSearchCriteria criteria = new AsyncSubmissionSearchCriteria();
        criteria.setSubmissionIds(Arrays.asList("sub1", "sub2"));

        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getAsyncSubmissionQuery(criteria, preparedStmtList);

        assertNotNull(query);
        assertEquals(2, preparedStmtList.size());
        assertEquals("sub1", preparedStmtList.get(0));
        assertEquals("sub2", preparedStmtList.get(1));
    }

    @Test
    void testGetAsyncSubmissionQuery_CourtIdOnly() {
        AsyncSubmissionSearchCriteria criteria = new AsyncSubmissionSearchCriteria();
        criteria.setCourtId("court1");

        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getAsyncSubmissionQuery(criteria, preparedStmtList);


        assertNotNull(query);
        assertEquals(1, preparedStmtList.size());
        assertEquals("court1", preparedStmtList.get(0));
    }

    @Test
    void testGetAsyncSubmissionQuery_JudgeIdsOnly() {
        AsyncSubmissionSearchCriteria criteria = new AsyncSubmissionSearchCriteria();
        criteria.setJudgeIds(Arrays.asList("judge1", "judge2"));

        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getAsyncSubmissionQuery(criteria, preparedStmtList);

        assertNotNull(query);
        assertEquals(2, preparedStmtList.size());
        assertEquals("judge1", preparedStmtList.get(0));
        assertEquals("judge2", preparedStmtList.get(1));
    }

    @Test
    void testGetAsyncSubmissionQuery_CaseIdsOnly() {
        AsyncSubmissionSearchCriteria criteria = new AsyncSubmissionSearchCriteria();
        criteria.setCaseIds(Arrays.asList("case1", "case2"));

        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getAsyncSubmissionQuery(criteria, preparedStmtList);


        assertNotNull(query);
        assertEquals(2, preparedStmtList.size());
        assertEquals("case1", preparedStmtList.get(0));
        assertEquals("case2", preparedStmtList.get(1));
    }

    @Test
    void testGetAsyncSubmissionQuery_SubmissionDateOnly() {
        AsyncSubmissionSearchCriteria criteria = new AsyncSubmissionSearchCriteria();
        criteria.setSubmissionDate(LocalDate.of(2023, 1, 1));

        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getAsyncSubmissionQuery(criteria, preparedStmtList);


        assertNotNull(query);
        assertEquals(1, preparedStmtList.size());
        assertEquals("2023-01-01", preparedStmtList.get(0));
    }

    @Test
    void testGetAsyncSubmissionQuery_ResponseDateOnly() {
        AsyncSubmissionSearchCriteria criteria = new AsyncSubmissionSearchCriteria();
        criteria.setResponseDate(LocalDate.of(2023, 1, 2));

        List<String> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getAsyncSubmissionQuery(criteria, preparedStmtList);


        assertNotNull(query);
        assertEquals(1, preparedStmtList.size());
        assertEquals("2023-01-02", preparedStmtList.get(0));
    }
}

package digit.repository;

import digit.repository.querybuilder.AsyncSubmissionQueryBuilder;
import digit.repository.rowmapper.AsyncSubmissionRowMapper;
import digit.web.models.AsyncSubmission;
import digit.web.models.AsyncSubmissionSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AsyncSubmissionRepositoryTest {

    @Mock
    private AsyncSubmissionQueryBuilder queryBuilder;

    @Mock
    private AsyncSubmissionRowMapper rowMapper;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AsyncSubmissionRepository repository;

    private AsyncSubmissionSearchCriteria searchCriteria;
    private List<AsyncSubmission> submissions;

    @BeforeEach
    void setUp(){
        searchCriteria = new AsyncSubmissionSearchCriteria();
        submissions = List.of(new AsyncSubmission());
    }
    @Test
    public void getAsyncSubmissions_Success() {
       searchCriteria.setCourtId("court");
       searchCriteria.setSubmissionDate(LocalDate.now());

       when(queryBuilder.getAsyncSubmissionQuery(searchCriteria, List.of())).thenReturn("SELECT * FROM async_submissions");
       when(jdbcTemplate.query(anyString(), any(Object[].class), any(AsyncSubmissionRowMapper.class))).thenReturn(submissions);

       submissions = repository.getAsyncSubmissions(searchCriteria);

       assertEquals(1, submissions.size());
       verify(queryBuilder).getAsyncSubmissionQuery(searchCriteria, List.of());
       verify(jdbcTemplate).query(anyString(), any(Object[].class), any(AsyncSubmissionRowMapper.class));
    }


    @Test
    public void getAsyncSubmissions_EmptyResult(){
        when(queryBuilder.getAsyncSubmissionQuery(searchCriteria, List.of())).thenReturn("SELECT * FROM async_submissions");
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(AsyncSubmissionRowMapper.class))).thenReturn(List.of());

        submissions = repository.getAsyncSubmissions(searchCriteria);

        assertEquals(0, submissions.size());
        verify(queryBuilder).getAsyncSubmissionQuery(searchCriteria, List.of());
        verify(jdbcTemplate).query(anyString(), any(Object[].class), any(AsyncSubmissionRowMapper.class));
    }
}

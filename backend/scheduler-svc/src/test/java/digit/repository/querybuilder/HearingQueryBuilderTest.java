package digit.repository.querybuilder;

import digit.helper.QueryBuilderHelper;
import digit.web.models.HearingSearchCriteria;
import digit.web.models.enums.EventType;
import digit.web.models.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HearingQueryBuilderTest {

    @Mock
    private QueryBuilderHelper queryBuilderHelper;

    @InjectMocks
    private HearingQueryBuilder hearingQueryBuilder;

    @Test
    public void testGetHearingQuery_withAllCriteria() {
        HearingSearchCriteria searchCriteria = new HearingSearchCriteria();
        searchCriteria.setTenantId("tenant1");
        searchCriteria.setJudgeId("judge1");
        searchCriteria.setCourtId("court1");
        searchCriteria.setCaseId("case1");
        searchCriteria.setHearingType(EventType.ADMISSION_HEARING.toString());
        searchCriteria.setFromDate(LocalDate.of(2023, 1, 1));
        searchCriteria.setToDate(LocalDate.of(2023, 12, 31));
        searchCriteria.setStartDateTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        searchCriteria.setEndDateTime(LocalDateTime.of(2023, 12, 31, 12, 0));
        List<Object> preparedStmtList = new ArrayList<>();

        String expectedQuery = "SELECT  hb.hearing_booking_id, hb.tenant_id, hb.court_id, hb.judge_id, hb.case_id, hb.hearing_date, hb.event_type, hb.title, hb.description, hb.status, hb.start_time, hb.end_time, hb.created_by,hb.last_modified_by,hb.created_time,hb.last_modified_time, hb.row_version ,hb.reschedule_request_id FROM hearing_booking hb  WHERE  hb.tenant_id = ?  AND  hb.judge_id = ?  AND  hb.court_id = ?  AND  hb.case_id = ?  AND  hb.event_type = ?  AND  TO_DATE(hb.hearing_date, 'YYYY-MM-DD')  >= ?  AND  TO_DATE(hb.hearing_date, 'YYYY-MM-DD') <= ?  AND  TO_TIMESTAMP(hb.start_time, 'YYYY-MM-DD HH24:MI:SS') >= ?  AND  TO_TIMESTAMP(hb.end_time , 'YYYY-MM-DD HH24:MI:SS') <= ? ";

        String actualQuery = hearingQueryBuilder.getHearingQuery(searchCriteria, preparedStmtList, null, null);

        assertEquals(9, preparedStmtList.size());

        verify(queryBuilderHelper, times(9)).addClauseIfRequired(any(StringBuilder.class), anyList());
    }

    @Test
    public void testGetHearingQuery_withNoCriteria() {
        HearingSearchCriteria searchCriteria = new HearingSearchCriteria();
        List<Object> preparedStmtList = new ArrayList<>();

        String expectedQuery = "SELECT  hb.hearing_booking_id, hb.tenant_id, hb.court_id, hb.judge_id, hb.case_id, hb.hearing_date, hb.event_type, hb.title, hb.description, hb.status, hb.start_time, hb.end_time, hb.created_by,hb.last_modified_by,hb.created_time,hb.last_modified_time, hb.row_version ,hb.reschedule_request_id FROM hearing_booking hb ";

        String actualQuery = hearingQueryBuilder.getHearingQuery(searchCriteria, preparedStmtList, null, null);

        assertEquals(0, preparedStmtList.size());

        verify(queryBuilderHelper, never()).addClauseIfRequired(any(StringBuilder.class), anyList());
    }

    @Test
    public void testGetJudgeAvailableDatesQuery_withAllCriteria() {
        HearingSearchCriteria searchCriteria = new HearingSearchCriteria();
        searchCriteria.setTenantId("tenant1");
        searchCriteria.setJudgeId("judge1");
        List<Object> preparedStmtList = new ArrayList<>();

        String expectedQuery = "SELECT meeting_hours.hearing_date AS date,meeting_hours.total_hours  AS hours FROM (SELECT hb.hearing_date, SUM(EXTRACT(EPOCH FROM (TO_TIMESTAMP(hb.end_time, 'YYYY-MM-DD HH24:MI:SS') - TO_TIMESTAMP(hb.start_time, 'YYYY-MM-DD HH24:MI:SS'))) / 3600) AS total_hours FROM hearing_booking hb  WHERE  hb.tenant_id = ?  AND  hb.judge_id = ? ) AS meeting_hours  ( hb.status = ?  OR hb.status = ? )";

        doNothing().when(queryBuilderHelper).addClauseIfRequired(any(StringBuilder.class), anyList());

        String actualQuery = hearingQueryBuilder.getJudgeAvailableDatesQuery(searchCriteria, preparedStmtList);

        assertEquals(4, preparedStmtList.size());


        verify(queryBuilderHelper, times(3)).addClauseIfRequired(any(StringBuilder.class), anyList());
    }

    @Test
    public void testGetJudgeAvailableDatesQuery_withNoCriteria() {
        HearingSearchCriteria searchCriteria = new HearingSearchCriteria();
        List<Object> preparedStmtList = new ArrayList<>();

        String expectedQuery = "SELECT meeting_hours.hearing_date AS date,meeting_hours.total_hours  AS hours FROM (SELECT hb.hearing_date, SUM(EXTRACT(EPOCH FROM (TO_TIMESTAMP(hb.end_time, 'YYYY-MM-DD HH24:MI:SS') - TO_TIMESTAMP(hb.start_time, 'YYYY-MM-DD HH24:MI:SS'))) / 3600) AS total_hours FROM hearing_booking hb  WHERE  ) AS meeting_hours  ( hb.status = ?  OR hb.status = ? )";

        doNothing().when(queryBuilderHelper).addClauseIfRequired(any(StringBuilder.class), anyList());
        String actualQuery = hearingQueryBuilder.getJudgeAvailableDatesQuery(searchCriteria, preparedStmtList);

        assertEquals(2, preparedStmtList.size());

        verify(queryBuilderHelper, times(1)).addClauseIfRequired(any(StringBuilder.class), anyList());
    }

}
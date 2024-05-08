package digit.repository.querybuilder;

import digit.helper.QueryBuilderHelper;
import digit.web.models.HearingSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Component
@Slf4j
public class HearingQueryBuilder {

    @Autowired
    private QueryBuilderHelper queryBuilderHelper;

    private final String BASE_APPLICATION_QUERY = "SELECT  hb.hearing_booking_id, hb.tenant_id, hb.court_id, hb.judge_id, hb.case_id, hb.hearing_date, hb.event_type, hb.title, hb.description, hb.status, hb.start_time, hb.end_time, hb.created_by,hb.last_modified_by,hb.created_time,hb.last_modified_time, hb.row_version ";

    private static final String FROM_TABLES = " FROM hearing_booking hb ";

    private final String ORDER_BY = " ORDER BY ";

    private final String GROUP_BY = " GROUP BY ";

    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";

    public String getHearingQuery(HearingSearchCriteria hearingSearchCriteria, List<Object> preparedStmtList) {

        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);

        getWhereFields(hearingSearchCriteria, query, preparedStmtList);

        return query.toString();
    }

    public String getJudgeAvailableDatesQuery(HearingSearchCriteria hearingSearchCriteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder("SELECT meeting_hours.hearing_date AS date,meeting_hours.total_hours  AS hours ");
        query.append("FROM (");
        query.append("SELECT hb.hearing_date, SUM(EXTRACT(EPOCH FROM (TO_TIMESTAMP(hb.end_time, 'YYYY-MM-DD HH24:MI:SS') - TO_TIMESTAMP(hb.start_time, 'YYYY-MM-DD HH24:MI:SS'))) / 3600) AS total_hours ");
        query.append("FROM hearing_booking hb ");

        getWhereFields(hearingSearchCriteria, query, preparedStmtList);

        query.append("GROUP BY hb.hearing_date) AS meeting_hours ");
        query.append("WHERE meeting_hours.total_hours < ? ");
        preparedStmtList.add(8);  //TODO:need to configure

        return query.toString();
    }


    private void getWhereFields(HearingSearchCriteria hearingSearchCriteria, StringBuilder query, List<Object> preparedStmtList) {


        if (!CollectionUtils.isEmpty(hearingSearchCriteria.getHearingIds())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.hearing_booking_id IN ( ").append(queryBuilderHelper.createQuery(hearingSearchCriteria.getHearingIds())).append(" ) ");
            queryBuilderHelper.addToPreparedStatement(preparedStmtList, hearingSearchCriteria.getHearingIds());
        }

        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getJudgeId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.judge_id = ? ");
            preparedStmtList.add(hearingSearchCriteria.getJudgeId());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getCourtId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.court_id = ? ");
            preparedStmtList.add(hearingSearchCriteria.getCourtId());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getCaseId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.case_id = ? ");
            preparedStmtList.add(hearingSearchCriteria.getCaseId());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getHearingType())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.event_type = ? ");
            preparedStmtList.add(hearingSearchCriteria.getHearingType());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getFromDate())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" TO_DATE(hb.hearing_date, 'YYYY-MM-DD')  >= ? ");
            preparedStmtList.add(hearingSearchCriteria.getFromDate());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getToDate())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" TO_DATE(hb.hearing_date, 'YYYY-MM-DD') <= ? ");
            preparedStmtList.add(hearingSearchCriteria.getToDate());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getStartDateTime())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" TO_TIMESTAMP(hb.start_time, 'YYYY-MM-DD HH24:MI:SS') >= ? ");
            preparedStmtList.add(hearingSearchCriteria.getStartDateTime());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getEndDateTime())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" TO_TIMESTAMP(hb.end_time , 'YYYY-MM-DD HH24:MI:SS') <= ? ");
            preparedStmtList.add(hearingSearchCriteria.getEndDateTime());

        }
    }


}

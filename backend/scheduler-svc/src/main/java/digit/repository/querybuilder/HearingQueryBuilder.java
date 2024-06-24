package digit.repository.querybuilder;

import digit.helper.QueryBuilderHelper;
import digit.web.models.HearingSearchCriteria;
import digit.web.models.enums.EventType;
import digit.web.models.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Component
@Slf4j
public class HearingQueryBuilder {

    private static final String FROM_TABLES = " FROM hearing_booking hb ";
    private final String BASE_APPLICATION_QUERY = "SELECT  hb.hearing_booking_id, hb.tenant_id, hb.court_id, hb.judge_id, hb.case_id, hb.hearing_date, hb.event_type, hb.title, hb.description, hb.status, hb.start_time, hb.end_time, hb.created_by,hb.last_modified_by,hb.created_time,hb.last_modified_time, hb.row_version ,hb.reschedule_request_id";
    private final String ORDER_BY = " ORDER BY ";
    private final String GROUP_BY = " GROUP BY ";
    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";
    @Autowired
    private QueryBuilderHelper queryBuilderHelper;

    public String getHearingQuery(HearingSearchCriteria hearingSearchCriteria, List<Object> preparedStmtList, Integer limit, Integer offset) {

        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);

        getWhereFields(hearingSearchCriteria, query, preparedStmtList, limit, offset);

        return query.toString();
    }

    public String getJudgeAvailableDatesQuery(HearingSearchCriteria hearingSearchCriteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder("SELECT meeting_hours.hearing_date AS date,meeting_hours.total_hours  AS hours ");
        query.append("FROM (");
        query.append("SELECT hb.hearing_date, SUM(EXTRACT(EPOCH FROM (TO_TIMESTAMP(hb.end_time, 'YYYY-MM-DD HH24:MI:SS') - TO_TIMESTAMP(hb.start_time, 'YYYY-MM-DD HH24:MI:SS'))) / 3600) AS total_hours ");
        query.append("FROM hearing_booking hb ");

        getWhereFields(hearingSearchCriteria, query, preparedStmtList, null, null);
        // add status block
        queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
        query.append(" ( hb.status = ? ");
        preparedStmtList.add(Status.BLOCKED.toString());
        query.append(" OR hb.status = ? )");
        preparedStmtList.add(Status.SCHEDULED.toString());


        query.append("GROUP BY hb.hearing_date) AS meeting_hours ");

        return query.toString();
    }


    private void getWhereFields(HearingSearchCriteria hearingSearchCriteria, StringBuilder query, List<Object> preparedStmtList, Integer limit, Integer offset) {


        if (!CollectionUtils.isEmpty(hearingSearchCriteria.getHearingIds())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.hearing_booking_id IN ( ").append(queryBuilderHelper.createQuery(hearingSearchCriteria.getHearingIds())).append(" ) ");
            queryBuilderHelper.addToPreparedStatement(preparedStmtList, hearingSearchCriteria.getHearingIds());
        }

        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getTenantId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.tenant_id = ? ");
            preparedStmtList.add(hearingSearchCriteria.getTenantId());

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

        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getRescheduleId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append("hb.reschedule_request_id = ? ");
            preparedStmtList.add(hearingSearchCriteria.getRescheduleId());
        }

        if (!CollectionUtils.isEmpty(hearingSearchCriteria.getStatus())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" ( ");
            for (int i = 0; i < hearingSearchCriteria.getStatus().size() - 1; i++) {
                query.append(" hb.status = ? ").append(" or ");
                preparedStmtList.add(hearingSearchCriteria.getStatus().get(i+1).toString());
            }
            query.append("hb.status = ? )");
            preparedStmtList.add(hearingSearchCriteria.getStatus().get(0).toString());

        }

        if (!ObjectUtils.isEmpty(limit) && !ObjectUtils.isEmpty(offset)) {
            query.append(LIMIT_OFFSET);
            preparedStmtList.add(limit);
            preparedStmtList.add(offset);
        }
    }


}

package digit.repository.querybuilder;

import digit.web.models.HearingSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;


import java.util.List;


@Component
@Slf4j
public class HearingQueryBuilder {

    private final String BASE_APPLICATION_QUERY = "SELECT  hb.hearingbookingid, hb.tenantid, hb.courtid, hb.judgeid, hb.caseid, hb.date, hb.eventtype, hb.title, hb.description, hb.status, hb.starttime, hb.endtime, hb.createdby,hb.lastmodifiedby,hb.createdtime,hb.lastmodifiedtime, hb.rowversion ";

    private static final String FROM_TABLES = " FROM hearing_booking hb ";

    private final String ORDER_BY = " ORDER BY ";

    private final String GROUP_BY = " GROUP BY ";

    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";

    public String getJudgeHearingQuery(HearingSearchCriteria hearingSearchCriteria, List<String> preparedStmtList) {

        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);

        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getJudgeId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.judgeId = ? ");
            preparedStmtList.add(hearingSearchCriteria.getJudgeId());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getCourtId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.courtid = ? ");
            preparedStmtList.add(hearingSearchCriteria.getCourtId());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getCaseId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.caseid = ? ");
            preparedStmtList.add(hearingSearchCriteria.getCaseId());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getHearingType())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.eventtype = ? ");
            preparedStmtList.add(hearingSearchCriteria.getHearingType());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getFromDate())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.date >= ? ");
            preparedStmtList.add(hearingSearchCriteria.getFromDate().toString());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getToDate().toString())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.date <= ? ");
            preparedStmtList.add(hearingSearchCriteria.getToDate().toString());

        }

        return query.toString();
    }

    public String getJudgeAvailableDatesQuery(HearingSearchCriteria hearingSearchCriteria, List<String> preparedStmtList) {
        StringBuilder query = new StringBuilder("SELECT hb.date ");
        query.append("FROM (");
        query.append("SELECT hb.date, SUM(TIMESTAMPDIFF(MINUTE, hb.starttime, hb.endtime)) / 60 AS total_hours");
        query.append(FROM_TABLES).append(GROUP_BY).append(" hb.date ");
        query.append(") AS meeting_hours ");

        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<String> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }


}

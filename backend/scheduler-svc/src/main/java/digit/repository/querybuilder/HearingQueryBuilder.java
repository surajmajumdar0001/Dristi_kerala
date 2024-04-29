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

    private final String BASE_APPLICATION_QUERY = "SELECT  hb.hearingbookingid, hb.tenantid, hb.courtid, hb.judgeid, hb.caseid, hb.date, hb.eventtype, hb.title, hb.description, hb.status, hb.starttime, hb.endtime, hb.createdby,hb.lastmodifiedby,hb.createdtime,hb.lastmodifiedtime, hb.rowversion ";

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
        StringBuilder query = new StringBuilder("SELECT meeting_hours.date ");
        query.append("FROM (");
        query.append("SELECT hb.date, SUM(EXTRACT(EPOCH FROM (hb.endtime - hb.starttime)) / 3600) AS total_hours ");
        query.append("FROM hearing_booking hb ");

        getWhereFields(hearingSearchCriteria, query, preparedStmtList);

        query.append("GROUP BY hb.date) AS meeting_hours ");
        query.append("WHERE meeting_hours.total_hours < ? ");
        preparedStmtList.add(8);  //TODO:need to configure

        return query.toString();
    }


    private void getWhereFields(HearingSearchCriteria hearingSearchCriteria, StringBuilder query, List<Object> preparedStmtList) {


        if (!CollectionUtils.isEmpty(hearingSearchCriteria.getHearingIds())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.hearingbookingid IN ( ").append(queryBuilderHelper.createQuery(hearingSearchCriteria.getHearingIds())).append(" ) ");
            queryBuilderHelper.addToPreparedStatement(preparedStmtList, hearingSearchCriteria.getHearingIds());
        }

        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getJudgeId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.judgeId = ? ");
            preparedStmtList.add(hearingSearchCriteria.getJudgeId());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getCourtId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.courtid = ? ");
            preparedStmtList.add(hearingSearchCriteria.getCourtId());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getCaseId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.caseid = ? ");
            preparedStmtList.add(hearingSearchCriteria.getCaseId());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getHearingType())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.eventtype = ? ");
            preparedStmtList.add(hearingSearchCriteria.getHearingType());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getFromDate())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.date >= ? ");
            preparedStmtList.add(hearingSearchCriteria.getFromDate());

        }
        if (!ObjectUtils.isEmpty(hearingSearchCriteria.getToDate())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hb.date <= ? ");
            preparedStmtList.add(hearingSearchCriteria.getToDate());

        }
    }


}

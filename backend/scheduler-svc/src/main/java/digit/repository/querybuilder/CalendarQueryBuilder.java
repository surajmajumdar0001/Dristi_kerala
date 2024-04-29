package digit.repository.querybuilder;

import digit.helper.QueryBuilderHelper;
import digit.web.models.SearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CalendarQueryBuilder {

    @Autowired
    private QueryBuilderHelper queryBuilderHelper;

    private final String BASE_APPLICATION_QUERY = "SELECT jc.judgeid, jc.id, jc.ruletype, jc.date, jc.notes, jc.createdby,jc.lastmodifiedby,jc.createdtime,jc.lastmodifiedtime, jc.rowversion ,jc.tenantid ";

    private static final String FROM_TABLES = " FROM judge_calendar_rules jc ";

    private final String ORDER_BY = " ORDER BY ";

    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";

    public String getJudgeCalendarQuery(SearchCriteria searchCriteria, List<Object> preparedStmtList) {

        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);

        if (searchCriteria.getTenantId() != null && !searchCriteria.getTenantId().isEmpty()) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" jc.tenantid = ? ");
            preparedStmtList.add(searchCriteria.getTenantId());
        }

        if (searchCriteria.getJudgeId() != null && !searchCriteria.getJudgeId().isEmpty()) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" jc.judgeid = ? ");
            preparedStmtList.add(searchCriteria.getJudgeId());
        }

        if (searchCriteria.getFromDate() != null) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" jc.date >= ? ");
            preparedStmtList.add(searchCriteria.getFromDate());
        }

        if (searchCriteria.getToDate() != null) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" jc.date <= ? ");
            preparedStmtList.add(searchCriteria.getToDate());
        }

        return query.toString();
    }

}

package digit.repository.querybuilder;

import digit.web.models.JudgeSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CalendarQueryBuilder {


    private final String BASE_APPLICATION_QUERY = "SELECT jc.judgeid, jc.id, jc.ruletype, jc.date, jc.notes, jc.createdby,jc.lastmodifiedby,jc.createdtime,jc.lastmodifiedtime, jc.rowversion ,jc.tenantid ";

    private static final String FROM_TABLES = " FROM judge_calendar_rules jc ";

    private final String ORDER_BY = " ORDER BY ";

    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";

    public String getJudgeCalendarQuery(JudgeSearchCriteria searchCriteria, List<String> preparedStmtList) {

        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);

        if (!ObjectUtils.isEmpty(searchCriteria.getJudgeId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" jc.judgeid = ? ");
            preparedStmtList.add(searchCriteria.getJudgeId());

        }

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

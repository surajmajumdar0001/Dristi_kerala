package digit.repository.querybuilder;

import digit.web.models.CalendarSearchCriteria;
import digit.web.models.JudgeAvailabilitySearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CalendarQueryBuilder {


    private final String BASE_APPLICATION_QUERY = "SELECT jc.judgeid, jc.id, jc.ruletype, jc.date, jc.notes, jc.createdby,jc.lastmodifiedby,jc.createdtime,jc.lastmodifiedtime, jc.rowversion ,jc.tenantid ";

    private static final String FROM_TABLES = " FROM judge_calendar_rules jc ";

    private final String ORDER_BY = " ORDER BY ";

    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";

    public String getJudgeCalendarQuery(JudgeAvailabilitySearchCriteria searchCriteria, List<String> preparedStmtList) {

        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);

        if (searchCriteria.getTenantId() != null && !searchCriteria.getTenantId().isEmpty()) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" tenantid = ? ");
            preparedStmtList.add(searchCriteria.getTenantId());
        }

        if (searchCriteria.getJudgeId() != null && !searchCriteria.getJudgeId().isEmpty()) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" judgeid = ? ");
            preparedStmtList.add(searchCriteria.getJudgeId());
        }

//        if (searchCriteria.getCourtId() != null && !searchCriteria.getCourtId().isEmpty()) {
//            addClauseIfRequired(query, preparedStmtList);
//            query.append(" courtid = ? ");
//            preparedStmtList.add(searchCriteria.getCourtId());
//        }

//        if (searchCriteria.getNumberOfSuggestedDays() != null) {
//            addClauseIfRequired(query, preparedStmtList);
//            query.append(" number_of_suggested_days = ? ");
//            preparedStmtList.add(searchCriteria.getNumberOfSuggestedDays());
//        }

//        if (searchCriteria.getFromDate() != null) {
//            addClauseIfRequired(query, preparedStmtList);
//            query.append(" from_date >= ? ");
//            preparedStmtList.add(searchCriteria.getFromDate());
//        }

//        if (searchCriteria.getToDate() != null) {
//            addClauseIfRequired(query, preparedStmtList);
//            query.append(" to_date <= ? ");
//            preparedStmtList.add(searchCriteria.getToDate());
//        }
//
//        if (searchCriteria.getDuration() != null) {
//            addClauseIfRequired(query, preparedStmtList);
//            query.append(" duration = ? ");
//            preparedStmtList.add(searchCriteria.getDuration());
//        }

        return query.toString();
    }

    public String getJudgeCalendarQuery(CalendarSearchCriteria searchCriteria, List<String> preparedStmtList) {

        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);

        if (searchCriteria.getTenantId() != null && !searchCriteria.getTenantId().isEmpty()) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" tenantid = ? ");
            preparedStmtList.add(searchCriteria.getTenantId());
        }

        if (searchCriteria.getJudgeId() != null && !searchCriteria.getJudgeId().isEmpty()) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" judgeid = ? ");
            preparedStmtList.add(searchCriteria.getJudgeId());
        }

//        if (searchCriteria.getCourtId() != null && !searchCriteria.getCourtId().isEmpty()) {
//            addClauseIfRequired(query, preparedStmtList);
//            query.append(" courtid = ? ");
//            preparedStmtList.add(searchCriteria.getCourtId());
//        }

//        if (searchCriteria.getFromDate() != null) {
//            addClauseIfRequired(query, preparedStmtList);
//            query.append(" from_date >= ? ");
//            preparedStmtList.add(String.valueOf(searchCriteria.getFromDate()));
//        }
//
//        if (searchCriteria.getToDate() != null) {
//            addClauseIfRequired(query, preparedStmtList);
//            query.append(" to_date <= ? ");
//            preparedStmtList.add(String.valueOf(searchCriteria.getToDate()));
//        }

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

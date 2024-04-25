package digit.repository.querybuilder;

import digit.web.models.AsyncSubmissionSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class AsyncSubmissionQueryBuilder {

    private final String BASE_ASYNC_SUBMISSION_QUERY = "SELECT as.court_id, as.case_id, as.async_submission_id, as.submission_type, as.title, as.description, as.status, as.created_by, as.created_time, as.last_modified_by, as.last_modified_time, as.row_version, as.tenant_id ";

    private static final String FROM_ASYNC_SUBMISSION = "FROM async_submission as";

    private final String ORDER_BY = " ORDER BY as.case_id, as.submission_type,";

    public String getAsyncSubmissionQuery(AsyncSubmissionSearchCriteria searchCriteria, List<String> preparedStmtList) {

        StringBuilder query = new StringBuilder(BASE_ASYNC_SUBMISSION_QUERY);
        query.append(FROM_ASYNC_SUBMISSION);

        if(!CollectionUtils.isEmpty(searchCriteria.getJudgeIds())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" as.judge_id IN ( ").append(createQuery(searchCriteria.getJudgeIds())).append(" ) ");
            addToPreparedStatement(preparedStmtList, searchCriteria.getJudgeIds());
        }
        if(!CollectionUtils.isEmpty(searchCriteria.getCaseIds())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" as.case_id IN ( ").append(createQuery(searchCriteria.getJudgeIds())).append(" ) ");
            addToPreparedStatement(preparedStmtList, searchCriteria.getCaseIds());
        }
        if (!ObjectUtils.isEmpty(searchCriteria.getSubmissionDate())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" cl.submission_date = ? ");
            preparedStmtList.add(searchCriteria.getSubmissionDate().toString());
        }
        if (!ObjectUtils.isEmpty(searchCriteria.getResponseDate())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" cl.response_date = ? ");
            preparedStmtList.add(searchCriteria.getResponseDate().toString());
        }
        query.append(ORDER_BY);

        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<String> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }

    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<String> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
}

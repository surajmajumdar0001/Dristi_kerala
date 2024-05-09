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

    private final String BASE_ASYNC_SUBMISSION_QUERY = "SELECT asb.court_id, asb.case_id, asb.judge_id,  asb.async_submission_id, asb.submission_type, asb.title, asb.description, asb.status, asb.submission_date, asb.response_date, asb.created_by, asb.created_time, asb.last_modified_by, asb.last_modified_time, asb.row_version, asb.tenant_id ";

    private static final String FROM_ASYNC_SUBMISSION = "FROM async_submission asb";

    private final String ORDER_BY = " ORDER BY asb.case_id, asb.submission_type";

    public String getAsyncSubmissionQuery(AsyncSubmissionSearchCriteria searchCriteria, List<String> preparedStmtList) {

        StringBuilder query = new StringBuilder(BASE_ASYNC_SUBMISSION_QUERY);
        query.append(FROM_ASYNC_SUBMISSION);

        if(!CollectionUtils.isEmpty(searchCriteria.getSubmissionIds())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" asb.async_submission_id IN ( ").append(createQuery(searchCriteria.getJudgeIds())).append(" ) ");
            addToPreparedStatement(preparedStmtList, searchCriteria.getSubmissionIds());
        }
        if (!ObjectUtils.isEmpty(searchCriteria.getCourtId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" asb.court_id = ? ");
            preparedStmtList.add(searchCriteria.getCourtId());
        }
        if(!CollectionUtils.isEmpty(searchCriteria.getJudgeIds())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" asb.judge_id IN ( ").append(createQuery(searchCriteria.getJudgeIds())).append(" ) ");
            addToPreparedStatement(preparedStmtList, searchCriteria.getJudgeIds());
        }
        if(!CollectionUtils.isEmpty(searchCriteria.getCaseIds())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" asb.case_id IN ( ").append(createQuery(searchCriteria.getJudgeIds())).append(" ) ");
            addToPreparedStatement(preparedStmtList, searchCriteria.getCaseIds());
        }
        if (!ObjectUtils.isEmpty(searchCriteria.getSubmissionDate())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" asb.submission_date = ? ");
            preparedStmtList.add(searchCriteria.getSubmissionDate().toString());
        }
        if (!ObjectUtils.isEmpty(searchCriteria.getResponseDate())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" asb.response_date = ? ");
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

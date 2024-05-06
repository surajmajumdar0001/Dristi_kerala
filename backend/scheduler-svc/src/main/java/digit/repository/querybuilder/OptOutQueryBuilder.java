package digit.repository.querybuilder;


import digit.helper.QueryBuilderHelper;
import digit.web.models.OptOutSearchCriteria;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class OptOutQueryBuilder {

    @Autowired
    private QueryBuilderHelper queryBuilderHelper;


    private final String BASE_APPLICATION_QUERY = "SELECT   oo.createdby,oo.lastmodifiedby,oo.createdtime,oo.lastmodifiedtime, oo.rowversion ";

    private static final String FROM_TABLES = " FROM opt_out oo ";

    private final String ORDER_BY = " ORDER BY ";

    private final String GROUP_BY = " GROUP BY ";

    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";

    public String getOptOutQuery(OptOutSearchCriteria optOutSearchCriteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);

        if (!CollectionUtils.isEmpty(optOutSearchCriteria.getIds())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" oo.id IN ( ").append(queryBuilderHelper.createQuery(optOutSearchCriteria.getIds())).append(" ) ");
            queryBuilderHelper.addToPreparedStatement(preparedStmtList, optOutSearchCriteria.getIds());
        }

        if (!ObjectUtils.isEmpty(optOutSearchCriteria.getJudgeId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" oo.judgeId = ? ");
            preparedStmtList.add(optOutSearchCriteria.getJudgeId());

        }
        if (!ObjectUtils.isEmpty(optOutSearchCriteria.getCaseId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" oo.caseid = ? ");
            preparedStmtList.add(optOutSearchCriteria.getCaseId());

        }

        if (!ObjectUtils.isEmpty(optOutSearchCriteria.getIndividualId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" oo.individualid = ? ");
            preparedStmtList.add(optOutSearchCriteria.getIndividualId());

        }
        if (!ObjectUtils.isEmpty(optOutSearchCriteria.getRescheduleRequestId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" oo.reschedulerequestid = ? ");
            preparedStmtList.add(optOutSearchCriteria.getRescheduleRequestId());

        }
        if (!ObjectUtils.isEmpty(optOutSearchCriteria.getTenantId())) {
            queryBuilderHelper.addClauseIfRequired(query, preparedStmtList);
            query.append(" oo.tenantid = ? ");
            preparedStmtList.add(optOutSearchCriteria.getTenantId());

        }


        return query.toString();


    }
}

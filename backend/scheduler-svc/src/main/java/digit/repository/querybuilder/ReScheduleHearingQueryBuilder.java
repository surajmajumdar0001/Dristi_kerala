package digit.repository.querybuilder;


import digit.helper.QueryBuilderHelper;
import digit.web.models.ReScheduleHearingReqSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@Slf4j
public class ReScheduleHearingQueryBuilder {


    @Autowired
    private QueryBuilderHelper helper;

    private final String BASE_APPLICATION_QUERY = "SELECT hbr.rescheduledRequestId, hbr.hearingBookingId, hbr.tenantId, hbr.judgeId, hbr.caseId,hbr.requesterId,hbr.reason,hbr.status,hbr.actionComment,hbr.documents, hbr.createdby,hbr.lastmodifiedby,hbr.createdtime,hbr.lastmodifiedtime, hbr.rowversion  ";

    private static final String FROM_TABLES = " FROM hearing_booking_reschedule_request hbr ";

    private final String ORDER_BY = " ORDER BY ";

    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";

    public String getReScheduleRequestQuery(ReScheduleHearingReqSearchCriteria searchCriteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);

        if (!CollectionUtils.isEmpty(searchCriteria.getRescheduledRequestId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.rescheduledRequestId IN ( ").append(helper.createQuery(searchCriteria.getRescheduledRequestId())).append(" ) ");
            helper.addToPreparedStatement(preparedStmtList, searchCriteria.getRescheduledRequestId());
        }

        if (!ObjectUtils.isEmpty(searchCriteria.getTenantId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.tenantid = ? ");
            preparedStmtList.add(searchCriteria.getTenantId());
        }

        if (!ObjectUtils.isEmpty(searchCriteria.getJudgeId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.judgeid = ? ");
            preparedStmtList.add(searchCriteria.getJudgeId());
        }

        if (!ObjectUtils.isEmpty(searchCriteria.getJudgeId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.caseId = ? ");
            preparedStmtList.add(searchCriteria.getJudgeId());
        }

        if (!ObjectUtils.isEmpty(searchCriteria.getHearingBookingId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.hearingBookingId = ? ");
            preparedStmtList.add(searchCriteria.getHearingBookingId());
        }
        if (!ObjectUtils.isEmpty(searchCriteria.getRequesterId())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.requesterId = ? ");
            preparedStmtList.add(searchCriteria.getRequesterId());
        }
        if (!ObjectUtils.isEmpty(searchCriteria.getStatus())) {
            helper.addClauseIfRequired(query, preparedStmtList);
            query.append(" hbr.status = ? ");
            preparedStmtList.add(searchCriteria.getStatus());
        }


        return query.toString();
    }
}

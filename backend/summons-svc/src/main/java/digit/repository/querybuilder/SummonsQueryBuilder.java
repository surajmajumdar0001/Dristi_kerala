package digit.repository.querybuilder;

import digit.web.models.SummonsSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SummonsQueryBuilder {

    private final String BASE_APPLICATION_QUERY = "SELECT su.summons_id, su.order_id, su.tenant_id, su.order_type, su.channel_name, su.is_accepted_by_channel, su.channel_acknowledgement_id, su.request_date, su.status_of_delivery, su.additional_fields, su.created_by, su.last_modified_by, su.created_time, su.last_modified_time, su.row_version ";

    private static final String FROM_TABLES = " FROM summons su ";

    public String getSummonsQuery(SummonsSearchCriteria searchCriteria, List<String> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);

        if (!ObjectUtils.isEmpty(searchCriteria.getOrderId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" su.order_id = ? ");
            preparedStmtList.add(searchCriteria.getOrderId());
        }
        if(!ObjectUtils.isEmpty(searchCriteria.getSummonsId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" su.summons_id = ? ");
            preparedStmtList.add(searchCriteria.getSummonsId());
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

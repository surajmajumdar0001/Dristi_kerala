package org.pucar.dristi.repository;

import lombok.extern.slf4j.Slf4j;
import org.pucar.dristi.model.EPostTrackerSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
@Slf4j
public class EPostQueryBuilder {

    private final String BASE_APPLICATION_QUERY = "SELECT process_number, tenant_id, file_store_id, task_number, tracking_number, pincode, address, delivery_status, remarks, additionalDetails, row_version, booking_date, received_date, createdBy, lastModifiedBy, createdTime, lastModifiedTime ";

    private static final String FROM_TABLES = " FROM dristi_epost_tracker ";

//    private final String ORDER_BY = " ORDER BY cl.case_date, cl.judge_id, cl.hearing_type";

    private final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";

    public String getEPostTracker(EPostTrackerSearchCriteria searchCriteria, List<Object> preparedStmtList, Integer limit, Integer offset){
        StringBuilder query = new StringBuilder(BASE_APPLICATION_QUERY);
        query.append(FROM_TABLES);
        if(!ObjectUtils.isEmpty(searchCriteria.getDeliveryStatus())){
            addClauseIfRequired(query,preparedStmtList);
            query.append(" delivery_status = ? ");
            preparedStmtList.add(searchCriteria.getDeliveryStatus());
        }
        if(!ObjectUtils.isEmpty(searchCriteria.getProcessNumber())){
            addClauseIfRequired(query,preparedStmtList);
            query.append(" process_number = ? ");
            preparedStmtList.add(searchCriteria.getProcessNumber());
        }
        if(!ObjectUtils.isEmpty(searchCriteria.getTrackingNumber())){
            addClauseIfRequired(query,preparedStmtList);
            query.append(" tracking_number = ? ");
            preparedStmtList.add(searchCriteria.getTrackingNumber());
        }
        if(!ObjectUtils.isEmpty(searchCriteria.getBookingDate())){
            addClauseIfRequired(query,preparedStmtList);
            query.append(" booking_date = ? ");
            preparedStmtList.add(searchCriteria.getTrackingNumber());
        }
        if(!ObjectUtils.isEmpty(searchCriteria.getReceivedDate())){
            addClauseIfRequired(query,preparedStmtList);
            query.append(" received_date = ? ");
            preparedStmtList.add(searchCriteria.getTrackingNumber());
        }
        if (!ObjectUtils.isEmpty(limit) && ObjectUtils.isEmpty(offset)) {
            query.append(LIMIT_OFFSET);
            preparedStmtList.add(limit);
            preparedStmtList.add(offset);
        }
        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }
}

package org.pucar.dristi.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.pucar.dristi.model.DeliveryStatus;
import org.pucar.dristi.model.EPostTracker;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.swing.tree.TreePath;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class EPostRowMapper implements RowMapper<EPostTracker> {


    @Override
    public EPostTracker mapRow(ResultSet rs, int rowNum) throws SQLException {
        String deliveryStatusStr = rs.getString("delivery_status");
        DeliveryStatus deliveryStatus = deliveryStatusStr != null ? DeliveryStatus.valueOf(deliveryStatusStr) : null;

        return EPostTracker.builder()
                .processNumber(rs.getString("process_number"))
                .tenantId(rs.getString("tenant_id"))
                .fileStoreId(rs.getString("file_store_id"))
                .taskNumber(rs.getString("task_number"))
                .trackingNumber(rs.getString("tracking_number"))
                .address(rs.getString("address"))
                .pinCode(rs.getString("pincode"))
                .deliveryStatus(deliveryStatus)
                .remarks(rs.getString("remarks"))
                .additionalDetails(rs.getObject("additionalDetails"))
                .rowVersion(rs.getInt("row_version"))
                .bookingDate(rs.getString("booking_date"))
                .receivedDate(rs.getString("received_date"))
                .auditDetails(
                        AuditDetails.builder()
                                .createdBy(rs.getString("createdBy"))
                                .lastModifiedBy(rs.getString("lastModifiedBy"))
                                .createdTime(rs.getLong("createdTime"))
                                .lastModifiedTime(rs.getLong("lastModifiedTime"))
                                .build()
                )
                .build();
    }
}

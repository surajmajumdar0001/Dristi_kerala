package org.pucar.dristi.model;

import lombok.*;
import org.egov.tracer.model.AuditDetails;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EPostTracker {
    private String processNumber;
    private String tenantId;
    private String taskNumber;
    private String trackingNumber;
    private String address;
    private String pinCode;
    private String deliveryStatus;
    private String remarks;
    private Object additionalDetails;
    private Integer rowVersion;
    private String bookingDate;
    private String receivedDate;
    private AuditDetails auditDetails;
}

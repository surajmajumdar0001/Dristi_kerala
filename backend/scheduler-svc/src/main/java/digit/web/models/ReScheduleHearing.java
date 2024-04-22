package digit.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.model.AuditDetails;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReScheduleHearing {

    @JsonProperty("hearingBookingId")
    private String hearingBookingId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("rescheduledRequestId")
    private String rescheduledRequestId;

    @JsonProperty("requesterId")
    private String requesterId;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("status")
    private String status;

    @JsonProperty("actionComment")
    private String actionComment;

    @JsonProperty("workflow")
    private Workflow workflow;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;


}

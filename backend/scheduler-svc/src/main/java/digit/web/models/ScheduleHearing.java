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
public class ScheduleHearing {

    @JsonProperty("hearingBookingId")
    private String hearingBookingId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("courtId")
    private String courtId;

    @JsonProperty("judgeId")
    private String judgeId;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("date")
    private String date;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("startTime")
    private String startTime;

    @JsonProperty("endTime")
    private String endTime;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("rowVersion")
    private Long rowVersion = null;
}

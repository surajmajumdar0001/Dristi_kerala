package digit.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.model.AuditDetails;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * JudgeCalendar
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-16T18:22:58.738027694+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JudgeCalendar {

    @JsonProperty("id")
    private String id;

    @JsonProperty("judgeId")
    private String judgeId;

    @JsonProperty("ruleType")
    private String ruleType;        // possible values ----> "NON-WORKING-DAY",  "LEAVE",  “OTHER"

    @JsonProperty("date")
    private LocalDateTime date;     // timestamp

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("rowVersion")
    private Long rowVersion = null;
}

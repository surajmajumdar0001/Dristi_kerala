package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.models.AuditDetails;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

/**
 * AsyncSubmission
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-29T18:15:41.472193800+05:30[Asia/Calcutta]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AsyncSubmission {

    @JsonProperty("submissionId")
    private String submissionId = null;

    @JsonProperty("tenantId")
    private String tenantId = null;

    @JsonProperty("courtId")
    private String courtId = null;

    @JsonProperty("judgeId")
    private String judgeId = null;

    @JsonProperty("caseId")
    private String caseId = null;

    @JsonProperty("submissionType")
    private String submissionType = null;

    @JsonProperty("title")
    private String title = null;

    @JsonProperty("description")
    private String description = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("submissionDate")
    @Valid
    private LocalDate submissionDate = null;

    @JsonProperty("responseDate")
    @Valid
    private LocalDate responseDate = null;

    @JsonProperty("auditDetails")
    @Valid
    private AuditDetails auditDetails = null;

    @JsonProperty("rowVersion")
    private Integer rowVersion = null;


}

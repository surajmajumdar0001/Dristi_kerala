package digit.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import digit.models.coremodels.AuditDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptOut {


    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("individualId")
    private String individualId;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("rescheduleRequestId")
    private String rescheduleRequestId;

    @JsonProperty("judgeId")
    private String judgeId;

    @JsonProperty("optOutDates")             // additional details
    private List<LocalDate> optoutDates;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("rowVersion")
    private Integer rowVersion = null;

}

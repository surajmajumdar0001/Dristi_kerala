package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


/**
 * JudgeAvailabilitySearchCriteria
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-16T18:22:58.738027694+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JudgeSearchCriteria {


    @JsonProperty("judgeId")
    private String judgeId;

    @JsonProperty("courtId")
    private String courtId;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("numberOfDays")
    private String numberOfDays;

    @JsonProperty("fromDate")
    private LocalDateTime fromDate;

    @JsonProperty("tenantId")
    @NotNull
    private String tenantId;


}

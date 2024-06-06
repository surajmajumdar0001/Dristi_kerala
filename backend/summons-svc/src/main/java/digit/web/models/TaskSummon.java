package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-05-29T13:38:04.562296+05:30[Asia/Calcutta]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskSummon {

    @JsonProperty("summonDetails")
    private SummonsDetails summonDetails = null;

    @JsonProperty("caseDetails")
    private CaseDetails caseDetails = null;

    @JsonProperty("respondentDetails")
    private RespondentDetails respondentDetails = null;

    @JsonProperty("deliveryChannel")
    private DeliveryChannel deliveryChannel = null;

    @JsonProperty("summonsDocument")
    private SummonsDocument summonsDocument;
}

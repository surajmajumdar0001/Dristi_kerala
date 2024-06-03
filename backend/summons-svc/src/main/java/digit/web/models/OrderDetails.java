package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.models.Address;
import org.springframework.validation.annotation.Validated;

@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-05-29T13:38:04.562296+05:30[Asia/Calcutta]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetails {

    @JsonProperty("orderId")
    @Valid
    private String orderId;

    @JsonProperty("issueDate")
    private String issueDate;

    @JsonProperty("issueType")
    private String issueType;

    @JsonProperty("caseTitle")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,512}$", message = "Title must be up to 512 alphanumeric characters")
    private String caseTitle;

    @JsonProperty("caseYear")
    private Integer caseYear;

    @JsonProperty("judgeName")
    @Pattern(regexp = "^[a-zA-Z]{1,100}$", message = "Judge name must be up to 100 alphabets with no numbers")
    private String judgeName;

    @JsonProperty("courtName")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,124}$", message = "Court name must be up to 250 alphanumeric characters")
    private String courtName;

    @JsonProperty("courtAddress")
    @Valid
    private Address courtAddress;

    @JsonProperty("issuedDetails")
    private String issuedDetails;
}
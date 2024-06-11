package drishti.payment.calculator.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.models.Address;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostalHub {

    @JsonProperty("hubId")
    private String hubId;

    @JsonProperty("pincode")
    private Integer pincode;

    @JsonProperty("name")
    private String name;

    @JsonProperty("tenantId")
    private String tenantId = null;

    @JsonProperty("rowVersion")
    private Integer rowVersion = null;


    @JsonProperty("address")
    private Address address;
}

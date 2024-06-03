package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Summon
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-05-29T13:38:04.562296+05:30[Asia/Calcutta]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SummonsDetails {

    @JsonProperty("orderDetails")
    private OrderDetails orderDetails = null;

    @JsonProperty("respondentDetails")
    private RespondentDetails respondentDetails = null;

    @JsonProperty("complainantDetails")
    private ComplainantDetails complainantDetails = null;

    @JsonProperty("deliveryChannels")
    private List<DeliveryChannel> channelsToDeliver = null;

    @JsonProperty("summonsDocument")
    private SummonsDocument summonsDocument = null;
}

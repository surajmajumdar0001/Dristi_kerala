package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SummonsSearchCriteria {

    @JsonProperty("orderId")
    private String orderId = null;

    @JsonProperty("summonsId")
    private String summonsId = null;
}

package digit.web.models;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.egov.common.contract.models.AuditDetails;

import java.time.LocalDateTime;

@Data
@Builder
public class Summons {

    @JsonProperty("summonsId")
    private String summonsId;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("orderType")
    private String orderType;

    @JsonProperty("channelName")
    private String channelName;

    @JsonProperty("isAcceptedByChannel")
    private Boolean isAcceptedByChannel;

    @JsonProperty("channelAcknowledgementId")
    private String channelAcknowledgementId;

    @JsonProperty("requestDate")
    private LocalDateTime requestDate;

    @JsonProperty("statusOfDelivery")
    private String statusOfDelivery;

    @JsonProperty("additionalFields")
    private AdditionalFields additionalFields;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("rowVersion")
    private Integer rowVersion;
}

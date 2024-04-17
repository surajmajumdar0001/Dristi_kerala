package digit.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.models.Workflow;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReScheduleHearing {

    @JsonProperty("hearingBookingId")
    private String hearingBookingId;

    @JsonProperty("rescheduledRequestId")
    private String rescheduledRequestId;

    @JsonProperty("requesterId")
    private String requesterId;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("status")
    private String status;

    @JsonProperty("actionComment")
    private String actionComment;


    private Workflow workflow;


}

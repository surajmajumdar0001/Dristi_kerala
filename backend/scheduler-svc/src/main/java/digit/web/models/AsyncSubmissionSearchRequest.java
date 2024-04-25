package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

/**
 * AsyncSubmissionSearchRequest
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-29T18:15:41.472193800+05:30[Asia/Calcutta]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AsyncSubmissionSearchRequest {

    @JsonProperty("requestInfo")
    @Valid
    private RequestInfo requestInfo = null;

    @JsonProperty("asyncSubmissionSearchCriteria")
    @Valid
    private AsyncSubmissionSearchCriteria asyncSubmissionSearchCriteria = null;


}

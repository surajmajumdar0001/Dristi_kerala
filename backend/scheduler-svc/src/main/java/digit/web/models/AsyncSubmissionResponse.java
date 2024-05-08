package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * AsyncSubmissionResponse
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-29T18:15:41.472193800+05:30[Asia/Calcutta]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AsyncSubmissionResponse {

    @JsonProperty("ResponseInfo")
    @Valid
    private ResponseInfo responseInfo = null;

    @JsonProperty("AsyncSubmissions")
    @Valid
    private List<AsyncSubmission> asyncSubmissions = null;


    public AsyncSubmissionResponse addAsyncSubmissionsItem(AsyncSubmission asyncSubmissionsItem) {
        if (this.asyncSubmissions == null) {
            this.asyncSubmissions = new ArrayList<>();
        }
        this.asyncSubmissions.add(asyncSubmissionsItem);
        return this;
    }

}

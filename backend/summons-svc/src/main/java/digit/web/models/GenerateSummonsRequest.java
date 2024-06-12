package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * SummonsRequest
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-05-29T13:38:04.562296+05:30[Asia/Calcutta]")
@Data
@Builder
public class GenerateSummonsRequest {

    @JsonProperty("RequestInfo")
    @Valid
    private RequestInfo requestInfo = null;

    @JsonProperty("TaskSummon")
    @Valid
    private TaskSummon taskSummon;
}

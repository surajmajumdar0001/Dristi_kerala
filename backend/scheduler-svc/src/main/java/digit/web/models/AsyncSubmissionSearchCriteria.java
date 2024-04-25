package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * AsyncSubmissionSearchCriteria
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-29T18:15:41.472193800+05:30[Asia/Calcutta]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AsyncSubmissionSearchCriteria {

    @JsonProperty("judgeIds")
    private List<String> judgeIds = null;

    @JsonProperty("caseIds")
    private List<String> caseIds = null;

    @JsonProperty("courtId")
    private String courtId = null;

    @JsonProperty("submissionDate")
    @Valid
    private LocalDate submissionDate = null;

    @JsonProperty("responseDate")
    @Valid
    private LocalDate responseDate = null;


    public AsyncSubmissionSearchCriteria addJudgeIdsItem(String judgeIdsItem) {
        if (this.judgeIds == null) {
            this.judgeIds = new ArrayList<>();
        }
        this.judgeIds.add(judgeIdsItem);
        return this;
    }

    public AsyncSubmissionSearchCriteria addCaseIdsItem(String caseIdsItem) {
        if (this.caseIds == null) {
            this.caseIds = new ArrayList<>();
        }
        this.caseIds.add(caseIdsItem);
        return this;
    }

}

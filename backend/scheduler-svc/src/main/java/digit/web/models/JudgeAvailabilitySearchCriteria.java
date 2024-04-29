package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JudgeAvailabilitySearchCriteria implements SearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;                        //required

    @JsonProperty("judgeId")
    private String judgeId;                         //required

    @JsonProperty("courtId")
    private String courtId;                         //required

    //TODO : need to configure
    @JsonProperty("numberOfSuggestedDays")
    private Integer numberOfSuggestedDays;

    @JsonProperty("fromDate")
    private LocalDate fromDate;

    @JsonProperty("toDate")
    private LocalDate toDate;

    @JsonProperty("duration")
    private Long duration;
}

package digit.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import digit.web.models.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HearingSearchCriteria {


    @JsonProperty("hearingIds")
    private List<String> hearingIds;

    @JsonProperty("judgeId")
    private String judgeId;

    @JsonProperty("courtId")
    private String courtId;

    @JsonProperty("fromDate")
    private LocalDate fromDate;

    @JsonProperty("toDate")
    private LocalDate toDate;

    //TODO: this should be enum
    @JsonProperty("hearingType")
    private String hearingType;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("tenantId")
    private String tenantId;
    // to search in a one date between hours (to make it more flexible search)
    @JsonProperty("startDateTime")
    private LocalDateTime startDateTime;

    @JsonProperty("endDateTime")
    private LocalDateTime endDateTime;

    @JsonProperty("status")
    private Status status;


}

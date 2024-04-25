package digit.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("fromDate")
    private LocalDate fromDate;

    @JsonProperty("judgeId")
    private String judgeId;

    @JsonProperty("courtId")
    private String courtId;

    @JsonProperty("toDate")
    private LocalDate toDate;

    @JsonProperty("hearingType")
    private String hearingType;

    @JsonProperty("caseId")
    private String caseId;

    @JsonProperty("tenantId")
    private String tenantId;



}

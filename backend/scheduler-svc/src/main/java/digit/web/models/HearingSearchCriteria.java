package digit.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HearingSearchCriteria {


    @JsonProperty("fromDate")
    private LocalDateTime fromDate;

    @JsonProperty("judgeId")
    private String judgeId;

    @JsonProperty("toDate")
    private LocalDateTime toDate;

    @JsonProperty("hearingType")
    private String hearingType;


}

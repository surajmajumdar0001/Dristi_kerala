package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Slot {
    @JsonProperty("id")
    private int id;

    @JsonProperty("slotName")
    private String slotName;

    @JsonProperty("slotStartTime")
    private String slotStartTime;

    @JsonProperty("slotEndTime")
    private String slotEndTime;

    @JsonProperty("slotDuration")
    private Integer slotDuration;

    @JsonProperty("unitOfMeasurement")
    private String unitOfMeasurement;
}


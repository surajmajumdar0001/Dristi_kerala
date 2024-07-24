package org.pucar.dristi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelReport {

    @JsonProperty("summonId")
    private String summonId;

    @JsonProperty("deliveryStatus")
    private String deliveryStatus;

    @JsonProperty("additionalFields")
    private AdditionalFields additionalFields;
}

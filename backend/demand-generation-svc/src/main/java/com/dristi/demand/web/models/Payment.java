package com.dristi.demand.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @JsonProperty("heading")
    private String heading;

    @JsonProperty("fields")
    private Map<String, String> fields;
}

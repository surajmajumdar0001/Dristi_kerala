package com.dristi.demand.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemandConfiguration {

    @JsonProperty("consumerId")
    private String consumerId;

    @JsonProperty("moduleCode")
    private String moduleCode;

    @JsonProperty("taxHeadMasterCode")
    private String taxHeadMasterCode;

    @JsonProperty("updateTopic")
    private String updateTopic;

    @JsonProperty("workflowAction")
    private String workflowAction;

    @JsonProperty("payment")
    private Object payment;
}
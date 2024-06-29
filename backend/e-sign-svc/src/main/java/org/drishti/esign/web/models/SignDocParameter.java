package org.drishti.esign.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignDocParameter {

    @JsonProperty("eSignResponse")
    private String eSignResponse;

    @JsonProperty("espTxnID")
    private String espTxnID;

    @JsonProperty("tenantId")
    private String tenantId;
}

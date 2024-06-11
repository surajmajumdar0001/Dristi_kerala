package drishti.payment.calculator.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IPostConfigParams {

    @JsonProperty("pageWeight")
    private int pageWeight;

    @JsonProperty("weightUnit")
    private String weightUnit;

    @JsonProperty("printingFeePerPage")
    private int printingFeePerPage;

    @JsonProperty("businessFee")
    private int businessFee;

    @JsonProperty("envelopeChargeIncludingGST")
    private int envelopeChargeIncludingGst;

    @JsonProperty("GSTPercentage")
    private int gstPercentage;

    @JsonProperty("courtFee")
    private int courtFee;

    @JsonProperty("applicationFee")
    private int applicationFee;

    @JsonProperty("speedPost")
    private SpeedPost speedPost;
}

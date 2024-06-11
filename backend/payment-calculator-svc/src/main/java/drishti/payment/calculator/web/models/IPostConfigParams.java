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

    @JsonProperty("weightPerPage")
    private int weightPerPage;

    @JsonProperty("printingFeePerPage")
    private int printingFeePerPage;

    @JsonProperty("businessFee")
    private int businessFee;

    @JsonProperty("envelopeFee")
    private int envelopeFee;

    @JsonProperty("gst")
    private double gst;
}

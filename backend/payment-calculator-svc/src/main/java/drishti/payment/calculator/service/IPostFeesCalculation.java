package drishti.payment.calculator.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import drishti.payment.calculator.util.MdmsUtil;
import drishti.payment.calculator.web.models.IPostConfigParams;
import org.springframework.beans.factory.annotation.Autowired;

public class IPostFeesCalculation {
    private final MdmsUtil mdmsUtil;

    @Autowired
    public IPostFeesCalculation(MdmsUtil mdmsUtil) {
        this.mdmsUtil = mdmsUtil;
    }


    public double calculateTotalFee(int numberOfPages, int distance, IPostConfigParams configParams) {
        int weightPerPage = configParams.getWeightPerPage();
        int printingFeePerPage = configParams.getPrintingFeePerPage();
        int businessFee = configParams.getBusinessFee();
        int envelopeFee = configParams.getEnvelopeFee();
        double gst = configParams.getGst();

        // Total Weight in grams
        int totalWeight = numberOfPages * weightPerPage;

        // Total Printing Fee
        int totalPrintingFee = numberOfPages * printingFeePerPage;

        // Speed Post Fee
        int speedPostFee = getSpeedPostFee(totalWeight, distance);

        // Total Fee before GST
        double totalFeeBeforeGST = totalWeight + totalPrintingFee + speedPostFee + businessFee;

        // GST Fee
        double gstFee = gst * totalFeeBeforeGST;

        // Total Fee
        return totalFeeBeforeGST + gstFee + envelopeFee;
    }

    public static double getSpeedPostFee(int weight, int distance) {
        JsonObject jsonObject = JsonParser.parseString(JSON_DATA).getAsJsonObject();
        JsonElement weightRanges = jsonObject.getAsJsonObject("speedPost").get("weightRanges");

        for (JsonElement weightRangeElement : weightRanges.getAsJsonArray()) {
            JsonObject weightRange = weightRangeElement.getAsJsonObject();
            double minWeight = weightRange.get("minWeight").getAsDouble();
            double maxWeight = weightRange.get("maxWeight").getAsDouble();

            if (weight >= minWeight && weight <= maxWeight) {
                JsonObject distanceRanges = weightRange.getAsJsonObject("distanceRanges");

                for (Map.Entry<String, JsonElement> entry : distanceRanges.entrySet()) {
                    JsonObject distanceRange = entry.getValue().getAsJsonObject();
                    double minDistance = distanceRange.get("minDistance").getAsDouble();
                    double maxDistance = distanceRange.get("maxDistance").getAsDouble();

                    if (distance >= minDistance && distance <= maxDistance) {
                        return distanceRange.get("fee").getAsDouble();
                    }
                }
            }
        }

        return -1; // If no matching range is found, return -1 or handle appropriately
    }

}

package drishti.payment.calculator.service;

import drishti.payment.calculator.util.IPostUtil;
import drishti.payment.calculator.web.models.DistanceRange;
import drishti.payment.calculator.web.models.IPostConfigParams;
import drishti.payment.calculator.web.models.SpeedPost;
import drishti.payment.calculator.web.models.WeightRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IPostFeesCalculation {
    private final IPostUtil iPostUtil;


    @Autowired
    public IPostFeesCalculation(IPostUtil iPostUtil) {
        this.iPostUtil = iPostUtil;

    }


    public void calculateFees() {

        IPostConfigParams iPostFeesDefaultData = iPostUtil.getIPostFeesDefaultData();
        int numberOfPages = 2;

        //todo: distance calculation
        int distance = 10;

        calculateTotalIPostFee(2, 10, iPostFeesDefaultData);

        calculateCourtFees(iPostFeesDefaultData);


    }

    private void calculateCourtFees(IPostConfigParams iPostFeesDefaultData) {
    }


    public double calculateTotalIPostFee(int numberOfPages, int distance, IPostConfigParams configParams) {
        int weightPerPage = configParams.getPageWeight();
        int printingFeePerPage = configParams.getPrintingFeePerPage();
        int businessFee = configParams.getBusinessFee();
        int envelopeFee = configParams.getEnvelopeChargeIncludingGst();

        SpeedPost speedPost = configParams.getSpeedPost();
        double gst = configParams.getGstPercentage();

        // Total Weight in grams
        int totalWeight = numberOfPages * weightPerPage;

        // Total Printing Fee
        int totalPrintingFee = numberOfPages * printingFeePerPage;

        // Speed Post Fee
        int speedPostFee = getSpeedPostFee(totalWeight, distance, speedPost);

        // Total Fee before GST
        double totalFeeBeforeGST = totalWeight + totalPrintingFee + speedPostFee + businessFee;

        // GST Fee
        double gstFee = gst * totalFeeBeforeGST;

        // Total Fee
        return totalFeeBeforeGST + gstFee + envelopeFee;
    }


    // Method to get speed post fee
    public int getSpeedPostFee(int weight, int distance, SpeedPost speedPost) {
        WeightRange weightRange = getWeightRange(weight, speedPost.getWeightRanges());

        assert weightRange != null;
        DistanceRange distanceRange = calculateDistanceRange(distance, weightRange);

        assert distanceRange != null;
        return distanceRange.getFee();
    }

    // Method to calculate weight range based on weight
    private WeightRange getWeightRange(int weight, List<WeightRange> weightRanges) {
        for (WeightRange range : weightRanges) {
            int lowerBound = range.getMinWeight();
            int upperBound = range.getMaxWeight();
            if (weight >= lowerBound && weight <= upperBound) {
                return range;
            }
        }
        return null; // Invalid weight range
    }

    // Method to calculate distance range based on distance
    private DistanceRange calculateDistanceRange(int distance, WeightRange weightRange) {
        Map<String, DistanceRange> distanceMap = weightRange.getDistanceRanges();
        for (DistanceRange range : distanceMap.values()) {
//                String[] bounds = range.split("-");
            int lowerBound = range.getMinDistance();
            int upperBound = range.getMaxDistance();
            if (distance >= lowerBound && distance <= upperBound) {
                return range;
            }
        }

        return null; // Invalid distance range
    }


}

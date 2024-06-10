package drishti.payment.calculator.service;

import drishti.payment.calculator.strategy.SummonsFeesCalculationStrategy;
import drishti.payment.calculator.util.MdmsUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class IPostFeesCalculation implements SummonsFeesCalculationStrategy {
    private final MdmsUtil mdmsUtil;

    @Autowired
    public IPostFeesCalculation(MdmsUtil mdmsUtil) {
        this.mdmsUtil = mdmsUtil;
    }

    @Override
    public void calculate() {

        mdmsUtil.fetchMdmsData()

    }
}

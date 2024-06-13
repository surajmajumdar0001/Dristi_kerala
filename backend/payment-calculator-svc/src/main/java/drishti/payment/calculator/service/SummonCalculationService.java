package drishti.payment.calculator.service;


import drishti.payment.calculator.factory.SummonContext;
import drishti.payment.calculator.factory.SummonFactory;
import drishti.payment.calculator.web.models.Calculation;
import drishti.payment.calculator.web.models.SummonCalculationCriteria;
import drishti.payment.calculator.web.models.SummonCalculationReq;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SummonCalculationService {

    private final SummonFactory summonFactory;

    @Autowired
    public SummonCalculationService(SummonFactory summonFactory) {
        this.summonFactory = summonFactory;
    }


    public List<Calculation> calculateSummonFees(SummonCalculationReq request) {
        List<SummonCalculationCriteria> calculationCriteria = request.getCalculationCriteria();
        RequestInfo requestInfo = request.getRequestInfo();
        List<Calculation> response = new ArrayList<>();
        for (SummonCalculationCriteria criteria : calculationCriteria) {
            String channelId = criteria.getChannelId();
            SummonPayment channel = summonFactory.getChannelById(channelId);

            SummonContext context = new SummonContext(channel);
            Calculation calculation = context.calculatePayment(requestInfo, criteria);
            response.add(calculation);
        }
        return response;


    }


}

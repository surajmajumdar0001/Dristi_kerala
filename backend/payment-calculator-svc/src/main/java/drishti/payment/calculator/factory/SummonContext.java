package drishti.payment.calculator.factory;

import drishti.payment.calculator.service.SummonPayment;
import drishti.payment.calculator.web.models.SummonCalculationReq;
import lombok.AllArgsConstructor;
import lombok.Setter;


@Setter
@AllArgsConstructor
public class SummonContext {

    private SummonPayment payment;

    public void calculatePayment(SummonCalculationReq request) {
        payment.calculatePayment(request);
    }
}

package drishti.payment.calculator.service;

import drishti.payment.calculator.web.models.SummonCalculationReq;

public interface SummonPayment {

    void calculatePayment(SummonCalculationReq request);

}

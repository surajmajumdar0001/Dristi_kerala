package drishti.payment.calculator.web.controllers;


import drishti.payment.calculator.service.CaseFeesCalculationService;
import drishti.payment.calculator.service.SummonCalculationService;
import drishti.payment.calculator.web.models.CalculationRes;
import drishti.payment.calculator.web.models.EFillingCalculationReq;
import drishti.payment.calculator.web.models.SummonCalculationReq;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-06-10T14:05:42.847785340+05:30[Asia/Kolkata]")
@Controller
@RequestMapping("")
public class PaymentApiController {

    private final CaseFeesCalculationService caseFeesService;
    private final SummonCalculationService summonCalculationService;

    @Autowired
    public PaymentApiController(CaseFeesCalculationService caseFeesService, SummonCalculationService summonCalculationService) {
        this.caseFeesService = caseFeesService;
        this.summonCalculationService = summonCalculationService;

    }

    @RequestMapping(value = "/v1/summons/_calculate", method = RequestMethod.POST)
    public ResponseEntity<CalculationRes> v1CalculatePost(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody SummonCalculationReq body) {
        summonCalculationService.calculateSummonFees();
        return null;

    }


    @RequestMapping(value = "/v1/case/fees/_calculate", method = RequestMethod.POST)
    public ResponseEntity<CalculationRes> caseFeesCalculation(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody EFillingCalculationReq body) {

        caseFeesService.calculateCaseFees();
        return null;

    }
}

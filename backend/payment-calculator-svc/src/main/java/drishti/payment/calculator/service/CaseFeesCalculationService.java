package drishti.payment.calculator.service;


import drishti.payment.calculator.config.Configuration;
import drishti.payment.calculator.util.EFillingUtil;
import drishti.payment.calculator.web.models.Calculation;
import drishti.payment.calculator.web.models.EFilingParam;
import drishti.payment.calculator.web.models.EFillingCalculationCriteria;
import drishti.payment.calculator.web.models.EFillingCalculationReq;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CaseFeesCalculationService {

    private final EFillingUtil eFillingUtil;
    private final DemandService demandService;
    private final Configuration config;

    @Autowired
    public CaseFeesCalculationService(EFillingUtil eFillingUtil, DemandService demandService, Configuration config) {
        this.eFillingUtil = eFillingUtil;
        this.demandService = demandService;
        this.config = config;
    }


    public List<Calculation> calculateCaseFees(EFillingCalculationReq request) {


        RequestInfo requestInfo = request.getRequestInfo();

        List<EFillingCalculationCriteria> calculationCriteria = request.getCalculationCriteria();

        EFilingParam eFillingDefaultData = eFillingUtil.getEFillingDefaultData(requestInfo, calculationCriteria.get(0).getTenantId());

        Double courtFee = eFillingDefaultData.getCourtFee();
        Double applicationFee = eFillingDefaultData.getApplicationFee();
        Double vakalathnamaFee = eFillingDefaultData.getVakalathnamaFee();
        Double advocateWelfareFund = eFillingDefaultData.getAdvocateWelfareFund();
        Double advocateClerkWelfareFund = eFillingDefaultData.getAdvocateClerkWelfareFund();
        Double petitionFeePercentage = eFillingDefaultData.getPetitionFeePercentage();
        Double defaultPetitionFee = eFillingDefaultData.getDefaultPetitionFee();


        List<Calculation> result = new ArrayList<>();

        for (EFillingCalculationCriteria criteria : calculationCriteria) {


            Double totalApplicationFee = criteria.getNumberOfApplication() * applicationFee;

            Double petitionFee = Math.max(defaultPetitionFee, petitionFeePercentage * criteria.getCheckAmount());


            Double totalCourtFee = courtFee + vakalathnamaFee + advocateWelfareFund + advocateClerkWelfareFund + totalApplicationFee + petitionFee;
            Calculation calculation = Calculation.builder()
                    .applicationId(criteria.getCaseId())
                    .totalAmount(totalCourtFee)
                    .tenantId(criteria.getTenantId()).build();

            result.add(calculation);
        }

        demandService.generateDemands(requestInfo, result,config.getCaseModuleCode(),config.getCaseTaxHeadMasterCode());
        return result;

    }

}

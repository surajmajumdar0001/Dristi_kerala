package digit.validator;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.Configuration;
import digit.repository.RescheduleRequestOptOutRepository;
import digit.repository.ServiceRequestRepository;
import digit.service.ReScheduleHearingService;
import digit.web.models.*;
import digit.web.models.cases.CaseCriteria;
import digit.web.models.cases.CaseSearchCriteria;
import digit.web.models.enums.Status;
import io.swagger.util.Json;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RescheduleRequestOptOutValidator {

    @Autowired
    private RescheduleRequestOptOutRepository repository;

    @Autowired
    private ReScheduleHearingService reScheduleHearingService;

    @Autowired
    private Configuration config;

    @Autowired
    private ServiceRequestRepository requestRepository;

    @Autowired
    ObjectMapper mapper;

    public void validateRequest(OptOutRequest request) {

        request.getOptOuts().forEach(application -> {
            if (ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException("DK_SH_APP_ERR", "tenantId is mandatory for opt out dates");
            if (ObjectUtils.isEmpty(application.getIndividualId()))
                throw new CustomException("DK_SH_APP_ERR", "individual id is mandatory for opt out dates");
            if (ObjectUtils.isEmpty(application.getRescheduleRequestId()))
                throw new CustomException("DK_SH_APP_ERR", "reschedule request id is mandatory for opt out dates");

            OptOutSearchCriteria optOutSearchCriteria = OptOutSearchCriteria.builder().individualId(application.getIndividualId()).rescheduleRequestId(application.getRescheduleRequestId()).build();
            List<OptOut> optOuts = repository.getOptOut(optOutSearchCriteria);
            if(!optOuts.isEmpty()){
                throw new CustomException("DK_SH_APP_ERR", "Opt out request already exists.");
            }

            //TODO:validate the person opting out have relation to case
            //for this call case api
            StringBuilder url = new StringBuilder(config.getCaseUrl() + config.getCaseEndpoint());
            CaseSearchCriteria caseSearchCriteria = CaseSearchCriteria.builder().RequestInfo(request.getRequestInfo()).tenantId("pg").criteria(Collections.singletonList(CaseCriteria.builder().caseId(application.getCaseId()).build())).build();
            Object response = requestRepository.postMethod(url, caseSearchCriteria);
            Set<String> representativeIds = new HashSet<>();
            try {
                JsonNode jsonNode = mapper.readTree(response.toString());
                JsonNode representativesNode = jsonNode.get("cases").get(0).get("representatives");
                if(representativesNode != null && representativesNode.isArray()){
                    for(JsonNode representative : representativesNode){
                        String representativeId = String.valueOf(representative.get("id").asText());
                        representativeIds.add(representativeId);
                    }
                }
            } catch (JsonProcessingException e) {
                throw new CustomException("DK_SH_APP_ERR", "Invalid json response.");
            }

            if(!representativeIds.contains(application.getIndividualId())){
                throw new CustomException("DK_SH_APP_ERR", "Invalid individualId.");
            }
        });

        // validate reschedule request exist in db
        List<String> ids = request.getOptOuts().stream().map((OptOut::getRescheduleRequestId)).toList();

        List<ReScheduleHearing> search = reScheduleHearingService.search(ReScheduleHearingReqSearchRequest.builder()
                .requestInfo(request.getRequestInfo())
                .criteria(ReScheduleHearingReqSearchCriteria.builder().rescheduledRequestId(ids).build()).build());

        if (ids.size() != search.size()) {
            throw new CustomException("DK_SH_APP_ERR", "Reschedule request does not exist in database");
        }
        Map<String, List<LocalDate>> resultMap = new HashMap<>();

        search.forEach((element) -> {
            if (!element.getStatus().equals(Status.APPROVED)) {
                throw new CustomException("Dk_SH_APP_ERR", "Opt-Out is not enable for reschedule request : " + element.getRescheduledRequestId());
            }
            resultMap.put(element.getRescheduledRequestId(), element.getSuggestedDates());
        });

        for (OptOut optOut : request.getOptOuts()) {

            Set<LocalDate> optoutDates = optOut.getOptoutDates().stream().map(LocalDate::from).collect(Collectors.toSet());
            String rescheduleRequestId = optOut.getRescheduleRequestId();

            if (resultMap.containsKey(rescheduleRequestId)) {
                resultMap.get(rescheduleRequestId)
                        .forEach(optoutDates::remove);
            }

            if (!optoutDates.isEmpty()) {
                throw new CustomException("DK_SH_APP_ERR", "opt out dates must be from suggested days");

            }

        }


    }

    public void validateUpdateRequest(OptOutRequest request) {


    }
}

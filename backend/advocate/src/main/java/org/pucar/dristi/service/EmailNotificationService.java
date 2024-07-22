package org.pucar.dristi.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.pucar.dristi.config.Configuration;
import org.pucar.dristi.config.ServiceConstants;
import org.pucar.dristi.kafka.Producer;
import org.pucar.dristi.web.models.AdvocateRequest;
import org.pucar.dristi.web.models.Email;
import org.pucar.dristi.web.models.EmailRequest;
import org.pucar.dristi.web.models.IndividualSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class EmailNotificationService {


    @Autowired
    private Producer producer;

    @Autowired
    private Configuration config;

    @Autowired
    private IndividualService individualService;

    public void sendEmailNotification(IndividualSearchRequest request, boolean isClerkUpdate) {
        String emailId = individualService.getEmailId(request.getRequestInfo(), request.getIndividual().getIndividualId(), new HashMap<>());
        if(emailId != null) {
            EmailRequest emailRequest = getEmailRequestBody(request, isClerkUpdate);
            emailRequest.getEmail().setEmailTo(new HashSet<>(Set.of(emailId)));
            sendEmailToKafka(emailRequest);
        }
    }

    public void sendEmailToKafka(EmailRequest emailRequest){
        producer.push(config.getEmailNotificationTopic(), emailRequest);
    }

    public EmailRequest getEmailRequestBody(IndividualSearchRequest request, boolean isClerkUpdate) {
        EmailRequest emailRequest = new EmailRequest();
        RequestInfo requestInfo = request.getRequestInfo();
        Email email = getEmail(request, isClerkUpdate);

        emailRequest.setRequestInfo(requestInfo);
        emailRequest.setEmail(email);

        return emailRequest;
    }

    private Email getEmail(IndividualSearchRequest request, boolean isClerkUpdate) {
        // setting email body based on the values in message
        String emailBody = "{\"individualId\": " + request.getIndividual().getIndividualId() + "}";
        String subject = isClerkUpdate ? ServiceConstants.ADVOCATE_UPDATE_SUBJECT : ServiceConstants.ADVOCATE_CLERK_UPDATE_SUBJECT;
        String templateCode = isClerkUpdate ? ServiceConstants.ADVOCATE_APPROVE : ServiceConstants.ADVOCATE_CLERK_APPROVE;

        Email email = new Email();
        email.setSubject(subject);
        email.setBody(emailBody);
        email.setTenantId(config.getStateLevelTenantId());
        email.setTemplateCode(templateCode);
        email.setHTML(true);
        return email;
    }
}
package org.pucar.dristi.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.pucar.dristi.config.Configuration;
import org.pucar.dristi.kafka.Producer;
import org.pucar.dristi.web.models.Email;
import org.pucar.dristi.web.models.EmailRequest;
import org.pucar.dristi.web.models.PendingTaskRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static org.pucar.dristi.config.ServiceConstants.PENDING_TASK_CASE;

@Service
@Slf4j
public class EmailNotificationService {


    @Autowired
    private Producer producer;

    @Autowired
    private Configuration config;

    public void sendEmailNotification(PendingTaskRequest request) {
        String emailId = request.getPendingTask().getAssignedTo().get(0).getEmailId();
        if(emailId != null) {
            EmailRequest emailRequest = getEmailRequestBody(request);
            emailRequest.getEmail().setEmailTo(new HashSet<>(Set.of(emailId)));
            sendEmailToKafka(emailRequest);
        }
    }

    public void sendEmailToKafka(EmailRequest emailRequest){
        producer.push(config.getEmailNotificationTopic(), emailRequest);
    }

    public EmailRequest getEmailRequestBody(PendingTaskRequest request) {
        EmailRequest emailRequest = new EmailRequest();
        RequestInfo requestInfo = request.getRequestInfo();
        Email email = getEmail(request);

        emailRequest.setRequestInfo(requestInfo);
        emailRequest.setEmail(email);

        return emailRequest;
    }

    private Email getEmail(PendingTaskRequest request) {
        // setting email body based on the values in message
        // configure subject and templatecode
        String emailBody = "{\"cnr\": "+ request.getPendingTask().getCnrNumber() +"}";
        String subject = "Case Pending for Action";
        String templateCode = PENDING_TASK_CASE;

        Email email = new Email();
        email.setSubject(subject);
        email.setBody(emailBody);
        email.setTenantId(config.getStateLevelTenantId());
        email.setTemplateCode(templateCode);
        email.setHTML(true);
        return email;
    }
}


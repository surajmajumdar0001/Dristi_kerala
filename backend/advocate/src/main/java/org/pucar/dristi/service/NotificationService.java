package org.pucar.dristi.service;

import lombok.extern.slf4j.Slf4j;
import org.pucar.dristi.config.Configuration;
import org.pucar.dristi.kafka.Producer;
import org.pucar.dristi.web.models.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private Producer producer;

    @Autowired
    private Configuration config;


    public void sendEmail(EmailRequest emailRequest) {
        producer.push(config.getEmailNotificationTopic(), emailRequest);
    }

}

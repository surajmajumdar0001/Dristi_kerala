package com.dristi.demand.kafka;

import com.dristi.demand.service.ProcessRequestService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class Consumer {

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private List<String> topics;

    public Consumer(List<String> topics) {
        this.topics = topics;
    }

    @PostConstruct
    public void start() {
        for (String topic : topics) {
            MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(topic);
            if (listenerContainer != null) {
                listenerContainer.start();
            }
        }
    }

    @Autowired
    private ProcessRequestService service;

    /*
     * Uncomment the below line to start consuming record from kafka.topics.consumer
     * Value of the variable kafka.topics.consumer should be overwritten in application.properties
     */
    @KafkaListener(topics = "#{'${kafka.topics}'.split(',')}")
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            service.process(record,topic);
        } catch (Exception e) {
            log.error("error",e);
            log.error("error occurred while processing the request");
        }

    }
}

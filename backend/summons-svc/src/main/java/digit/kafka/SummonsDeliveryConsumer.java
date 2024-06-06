package digit.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.service.SummonsService;
import digit.web.models.SummonsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
@EnableAsync
public class SummonsDeliveryConsumer {

    @Autowired
    private SummonsService summonsService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "update-summons-topic")
    @Async
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            SummonsRequest request = objectMapper.convertValue(record, SummonsRequest.class);
            log.info(request.toString());
            summonsService.processStatusAndUpdateSummonsTask(request);
        } catch (final Exception e) {
            log.error("Error while listening to value: " + record + ": ", e);
        }
    }
}

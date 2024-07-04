package digit.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.service.SummonsService;
import digit.web.models.TaskRequest;
import digit.web.models.TaskResponse;
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
public class GenerateSummonsConsumer {

    private final SummonsService summonsService;

    private final ObjectMapper objectMapper;

    @Autowired
    public GenerateSummonsConsumer(SummonsService summonsService, ObjectMapper objectMapper) {
        this.summonsService = summonsService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "generate-summons-document")
    @Async
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            TaskRequest taskRequest = objectMapper.convertValue(record, TaskRequest.class);
            log.info(taskRequest.getTask().toString());
            summonsService.generateSummonsDocument(taskRequest);
        } catch (final Exception e) {
            log.error("Error while listening to value: " + record + ": ", e);
        }
    }
}

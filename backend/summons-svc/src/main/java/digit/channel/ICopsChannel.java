package digit.channel;

import digit.config.Configuration;
import digit.web.models.ChannelMessage;
import digit.web.models.GenerateSummonsRequest;
import digit.web.models.SendSummonsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ICopsChannel implements ExternalChannel {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Configuration config;

    @Override
    public ChannelMessage sendSummons(SendSummonsRequest request) {
        StringBuilder uri = new StringBuilder();
        uri.append(config.getICopsHost())
                .append(config.getICopsRequestEndPoint());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SendSummonsRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<ChannelMessage> responseEntity = restTemplate.postForEntity(uri.toString(),
                requestEntity, ChannelMessage.class);

        return responseEntity.getBody();
    }
}

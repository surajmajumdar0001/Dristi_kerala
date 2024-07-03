package digit.channel;

import digit.config.Configuration;
import digit.web.models.ChannelMessage;
import digit.web.models.ChannelResponse;
import digit.web.models.SendSummonsRequest;
import digit.web.models.TaskRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ESummonsChannel implements ExternalChannel{


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Configuration config;

    @Override
    public ChannelMessage sendSummons(TaskRequest request) {
        StringBuilder uri = new StringBuilder();
        uri.append(config.getESummonsHost()).append(config.getESummonsRequestEndPoint());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SendSummonsRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<ChannelResponse> responseEntity = restTemplate.postForEntity(uri.toString(),
                requestEntity, ChannelResponse.class);

        return responseEntity.getBody().getChannelMessage();
    }
}

package digit.channel;

import digit.config.Configuration;
import digit.web.models.DeliveryChannel;
import digit.web.models.Summons;
import digit.web.models.SummonsDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    public void sendSummons(SummonsDetails summonsDetails, Summons summons, DeliveryChannel deliveryChannel) {
        StringBuilder uri = new StringBuilder();
        uri.append(config.getESummonsHost()).append(config.getESummonsRequestEndPoint());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return;
    }
}

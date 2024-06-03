package digit.util;

import digit.config.Configuration;
import digit.web.models.OrderStatusUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class OrdersServiceUtil {

    private final RestTemplate restTemplate;

    private final Configuration config;

    public OrdersServiceUtil(RestTemplate restTemplate, Configuration config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public void updateOrdersStatus(OrderStatusUpdateRequest orderStatusUpdateRequest) {
        try {
            StringBuilder uri = new StringBuilder();
            uri.append(config.getOrdersServiceHost())
                    .append(config.getOrdersServiceEndpoint()).append("?tenantId=").append(config.getEgovStateTenantId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<OrderStatusUpdateRequest> requestEntity = new HttpEntity<>(orderStatusUpdateRequest, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri.toString(),
                    requestEntity, String.class);

        } catch (Exception e) {
            log.error("Error getting response from Orders Service", e);
            throw new CustomException("SU_PDF_APP_ERROR", "Error getting response from Orders Service");
        }
    }
}

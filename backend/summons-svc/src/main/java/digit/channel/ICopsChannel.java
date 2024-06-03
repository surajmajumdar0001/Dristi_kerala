package digit.channel;

import digit.web.models.DeliveryChannel;
import digit.web.models.Summons;
import digit.web.models.SummonsDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ICopsChannel implements ExternalChannel{
    @Override
    public void sendSummons(SummonsDetails summonsDetails, Summons summons, DeliveryChannel deliveryChannel) {

    }
}

package digit.channel;

import digit.web.models.DeliveryChannel;
import digit.web.models.Summons;
import digit.web.models.SummonsDetails;

public class EPostChannel implements ExternalChannel{
    @Override
    public void sendSummons(SummonsDetails summonsDetails, Summons summons, DeliveryChannel deliveryChannel) {

    }
}

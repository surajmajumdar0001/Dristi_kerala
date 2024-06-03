package digit.util;

import digit.channel.ChannelFactory;
import digit.channel.ExternalChannel;
import digit.web.models.DeliveryChannel;
import digit.web.models.Summons;
import digit.web.models.SummonsDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExternalChannelUtil {

    private final ChannelFactory channelFactory;

    public ExternalChannelUtil(ChannelFactory channelFactory) {
        this.channelFactory = channelFactory;
    }

    public void sendSummonsByDeliveryChannel(SummonsDetails summonsDetails, Summons summons, DeliveryChannel deliveryChannel) {
        ExternalChannel externalDeliveryChannel = channelFactory.getDeliveryChannel(deliveryChannel.getChannelName());
        externalDeliveryChannel.sendSummons(summonsDetails, summons, deliveryChannel);
    }
}

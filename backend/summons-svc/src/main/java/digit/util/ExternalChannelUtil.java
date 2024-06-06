package digit.util;

import digit.channel.ChannelFactory;
import digit.channel.ExternalChannel;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExternalChannelUtil {

    private final ChannelFactory channelFactory;

    public ExternalChannelUtil(ChannelFactory channelFactory) {
        this.channelFactory = channelFactory;
    }

    public ChannelMessage sendSummonsByDeliveryChannel(SendSummonsRequest request, SummonsDelivery summonsDelivery) {
        ExternalChannel externalDeliveryChannel = channelFactory.getDeliveryChannel(summonsDelivery.getChannelName());
        return externalDeliveryChannel.sendSummons(request);
    }
}

package digit.channel;

import digit.config.Configuration;
import digit.web.models.ChannelName;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ChannelFactory {

    private final ICopsChannel iCopsChannel;

    private final ESummonsChannel eSummonsChannel;

    @Autowired
    public ChannelFactory(ICopsChannel iCopsChannel, ESummonsChannel eSummonsChannel) {
        this.iCopsChannel = iCopsChannel;
        this.eSummonsChannel = eSummonsChannel;
    }

    public ExternalChannel getDeliveryChannel(ChannelName channelName) {
        return switch (channelName) {
            case EPOST -> eSummonsChannel;
            case ICOPS -> iCopsChannel;
            case ESUMMONS -> new ESummonsChannel();
            default ->
                    throw new CustomException("SUMMONS_UNKNOWN_DELIVERY_CHANNEL", "Delivery Channel provided is not Valid");
        };
    }
}

package digit.channel;

import digit.web.models.ChannelName;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        switch (channelName) {
            case EPOST:
                return new EPostChannel();
            case ICOPS:
                return new ICopsChannel();
            case ESUMMONS:
                return new ESummonsChannel();
            default:
                throw new CustomException("SUMMONS_UNKNOWN_DELIVERY_CHANNEL", "Delivery Channel provided is not Valid");
        }
    }
}

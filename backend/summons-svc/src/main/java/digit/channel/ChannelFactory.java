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
        return switch (channelName) {
            case POST -> eSummonsChannel;
            case POLICE -> iCopsChannel;
            case SMS -> new ESummonsChannel();
            default ->
                    throw new CustomException("SUMMONS_UNKNOWN_DELIVERY_CHANNEL", "Delivery Channel provided is not Valid");
        };
    }
}

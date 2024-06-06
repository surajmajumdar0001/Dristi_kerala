package digit.channel;

import digit.web.models.ChannelMessage;
import digit.web.models.SendSummonsRequest;

public interface ExternalChannel {

    ChannelMessage sendSummons(SendSummonsRequest request);
}

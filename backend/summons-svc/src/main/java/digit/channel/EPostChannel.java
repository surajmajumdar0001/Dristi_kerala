package digit.channel;

import digit.web.models.ChannelMessage;
import digit.web.models.SendSummonsRequest;

public class EPostChannel implements ExternalChannel{
    @Override
    public ChannelMessage sendSummons(SendSummonsRequest request) {
        return null;
    }
}

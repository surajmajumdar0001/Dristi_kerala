package digit.channel;

import digit.web.models.ChannelMessage;
import digit.web.models.TaskRequest;

public class EPostChannel implements ExternalChannel{
    @Override
    public ChannelMessage sendSummons(TaskRequest request) {
        return null;
    }
}

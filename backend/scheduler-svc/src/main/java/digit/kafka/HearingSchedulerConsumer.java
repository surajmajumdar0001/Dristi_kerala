package digit.kafka;


import digit.service.BlockedCalendarUpdate;
import digit.service.HearingScheduler;
import digit.service.OptOutHearingSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class HearingSchedulerConsumer {

    @Autowired
    private BlockedCalendarUpdate blockedCalendarUpdate;

    @Autowired
    private OptOutHearingSchedule optOutHearingSchedule;

    @KafkaListener(topics = {"schedule-hearing-to-block-calendar"})
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {
            blockedCalendarUpdate.updateRequestForBlockCalendar(record);

        } catch (Exception e) {

            log.error("error occurred while serializing", e);

        }

    }


    @KafkaListener(topics = {"check-opt-out"})
    public void listenOptOut(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {
            optOutHearingSchedule.checkAndScheduleHearingForOptOut(record);

        } catch (Exception e) {

            log.error("error occurred while serializing", e);

        }

    }
}

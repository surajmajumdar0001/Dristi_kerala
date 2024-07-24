package org.pucar.dristi.task;

import lombok.extern.slf4j.Slf4j;
import org.pucar.dristi.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableScheduling
public class PaymentPendingScheduler {
    @Autowired
    NotificationService notificationService;
    @Scheduled(cron = "dristi.cron.payment.due.date", zone = "Asia/Kolkata")
    public void paymentPendingNotification(){
        notificationService.sendNotification(null,null);
    }

}

package digit.config;


import lombok.*;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;


@Component
@Data
@Import({TracerConfiguration.class})
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Configuration {

    @Value("${drishti.scheduler.hearing}")
    private String scheduleHearingTopic;

    @Value("${drishti.scheduler.opt-out}")
    private String optOutTopic;

    @Value("${drishti.scheduler.opt-out.update}")
    private String optOutUpdateTopic;

    @Value("${drishti.scheduler.hearing.update}")
    private String scheduleHearingUpdateTopic;

    @Value("${drishti.scheduler.hearing.reschedule}")
    private String rescheduleRequestCreateTopic;

    @Value("${drishti.judge.calendar.update}")
    private String updateJudgeCalendarTopic;

    @Value("${drishti.scheduler.hearing.reschedule.update}")
    private String updateRescheduleRequestTopic;

    // User Config
    @Value("${egov.user.host}")
    private String userHost;

    @Value("${egov.user.context.path}")
    private String userContextPath;

    @Value("${egov.user.create.path}")
    private String userCreateEndpoint;

    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.user.update.path}")
    private String userUpdateEndpoint;


    //Idgen Config
    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;


    // id format
    @Value("${egov.idgen.idformat}")
    private String hearingIdFormat;

    @Value("${egov.idgen.reschedule}")
    private String rescheduleHearingIdFormat;


    //Workflow Config
    @Value("${egov.workflow.host}")
    private String wfHost;

    @Value("${egov.workflow.transition.path}")
    private String wfTransitionPath;

    @Value("${egov.workflow.businessservice.search.path}")
    private String wfBusinessServiceSearchPath;

    @Value("${egov.workflow.processinstance.search.path}")
    private String wfProcessInstanceSearchPath;


    //MDMS
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndPoint;

    //SMSNotification
    @Value("${egov.sms.notification.topic}")
    private String smsNotificationTopic;


}

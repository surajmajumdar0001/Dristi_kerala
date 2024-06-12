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

    //Tenant Id
    @Value("${egov-state-level-tenant-id}")
    private String egovStateTenantId;

    //Idgen Config
    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

    //Pdf Service Config
    @Value("${summons.pdf.template.key}")
    private String summonsPdfTemplateKey;

    //MDMS
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndPoint;

    //SMSNotification
    @Value("${egov.sms.notification.topic}")
    private String smsNotificationTopic;

    @Value("${egov.pdf.service.host}")
    private String pdfServiceHost;

    @Value("${egov.pdf.service.create.endpoint}")
    private String pdfServiceEndpoint;

    // File Store Service
    @Value("${egov.file.store.summons.module}")
    private String summonsFileStoreModule;

    @Value("${egov.file.store.host}")
    private String fileStoreHost;

    @Value("${egov.file.store.save.endpoint}")
    private String fileStoreEndPoint;

    // task service

    @Value("${egov.task.service.host}")
    private String taskServiceHost;

    @Value("${egov.task.service.update.endpoint}")
    private String taskServiceUpdateEndpoint;

    // ICops

    @Value("${egov.icops.host}")
    private String iCopsHost;

    @Value("${egov.icops.request.endpoint}")
    private String iCopsRequestEndPoint;

    // ESummons

    @Value("${egov.esummons.host}")
    private String eSummonsHost;

    @Value("${egov.esummons.request.endpoint}")
    private String ESummonsRequestEndPoint;
}

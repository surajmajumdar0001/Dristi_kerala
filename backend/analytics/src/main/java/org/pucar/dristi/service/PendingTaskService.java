package org.pucar.dristi.service;


import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.pucar.dristi.config.Configuration;
import org.pucar.dristi.util.IndexerUtils;
import org.pucar.dristi.web.models.PendingTask;
import org.pucar.dristi.web.models.PendingTaskRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.pucar.dristi.config.ServiceConstants.*;

@Service
@Slf4j
public class PendingTaskService {

    private final Configuration config;
    private final IndexerUtils indexerUtils;
    private final EmailNotificationService emailNotificationService;

    @Autowired
    public PendingTaskService(Configuration config, IndexerUtils indexerUtils, EmailNotificationService emailNotificationService) {
        this.config = config;
        this.indexerUtils = indexerUtils;
        this.emailNotificationService = emailNotificationService;
    }

    public PendingTask createPendingTask(PendingTaskRequest pendingTaskRequest) {
        try {

            log.info("Inside Pending Task service:: PendingTaskRequest: {}", pendingTaskRequest);
            if(pendingTaskRequest.getPendingTask().getEntityType().equals("case")) {
                emailNotificationService.sendEmailNotification(pendingTaskRequest);
            }
            String bulkRequest = indexerUtils.buildPayload(pendingTaskRequest.getPendingTask());
            if (!bulkRequest.isEmpty()) {
                String uri = config.getEsHostUrl() + config.getBulkPath();
                indexerUtils.esPostManual(uri, bulkRequest);
            }
            return pendingTaskRequest.getPendingTask();
        } catch (CustomException e) {
            log.error("Custom Exception occurred while creating hearing");
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while creating hearing");
            throw new CustomException(Pending_Task_Exception, e.getMessage());
        }
    }

}

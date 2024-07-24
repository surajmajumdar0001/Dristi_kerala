package org.pucar.dristi.util;

import org.pucar.dristi.config.EPostConfiguration;
import org.pucar.dristi.model.EPostTracker;
import org.pucar.dristi.model.TaskRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class EpostUtil {

    private final IdgenUtil idgenUtil;

    private final EPostConfiguration config;

    @Autowired
    public EpostUtil(IdgenUtil idgenUtil, EPostConfiguration config) {
        this.idgenUtil = idgenUtil;
        this.config = config;
    }

    public EPostTracker createPostTrackerBody(TaskRequest request) {
        String processNumber = idgenUtil.getIdList(request.getRequestInfo(), config.getEgovStateTenantId(),
                config.getIdName(),null,1).get(0);
        //TODO fill remaining columns
        return EPostTracker.builder()
                .processNumber(processNumber)
                .tenantId(config.getEgovStateTenantId())
                .build();
    }
}

package org.pucar.dristi.service;

import org.pucar.dristi.config.EPostConfiguration;
import org.pucar.dristi.model.ChannelMessage;
import org.pucar.dristi.model.EPostTracker;
import org.pucar.dristi.model.TaskRequest;
import org.pucar.dristi.repository.EPostRepository;
import org.pucar.dristi.util.IdgenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EPostService {
    @Autowired
    private IdgenUtil idgenUtil;
    @Autowired
    private EPostConfiguration configuration;
    @Autowired
    private EPostRepository ePostRepository;
    public ChannelMessage sendEPost(TaskRequest body){
        String processNumber = idgenUtil.getIdList(body.getRequestInfo(),configuration.getEgovStateTenantId(),configuration.getIdName(),null,1).get(0);
        ePostRepository.sendEPost(body,processNumber);
        return null;
    }
    public List<EPostTracker> getEPost(TaskRequest body){
        return null;
    }
    public EPostTracker updateEPost(EPostTracker ePostTracker){
        return null;
    }
}

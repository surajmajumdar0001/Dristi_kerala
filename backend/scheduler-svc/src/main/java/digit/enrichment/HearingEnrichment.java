package digit.enrichment;


import digit.config.Configuration;
import digit.models.coremodels.AuditDetails;
import digit.util.IdgenUtil;
import digit.web.models.ScheduleHearing;
import digit.web.models.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class HearingEnrichment {

    @Autowired
    private IdgenUtil idgenUtil;

    @Autowired
    private Configuration configuration;


    public void enrichScheduleHearing(RequestInfo requestInfo, List<ScheduleHearing> hearingList) {

        log.info("starting update method for schedule hearing enrichment");
        log.info("generating IDs for schedule hearing enrichment using IdGenService");
        List<String> idList = idgenUtil.getIdList(requestInfo,
                hearingList.get(0).getTenantId(),
                configuration.getHearingIdFormat(), null, hearingList.size());
        AuditDetails auditDetails = getAuditDetailsScheduleHearing(requestInfo);
        int index = 0;
        for (ScheduleHearing hearing : hearingList) {
            hearing.setAuditDetails(auditDetails);
            hearing.setHearingBookingId(idList.get(index++));
            hearing.setRowVersion(1);
            hearing.setStatus(Status.SCHEDULED);
        }




    }


    public void enrichUpdateScheduleHearing(RequestInfo requestInfo, List<ScheduleHearing> hearingList) {

        hearingList.stream().forEach((hearing) -> {

            Long currentTime = System.currentTimeMillis();
            hearing.getAuditDetails().setLastModifiedTime(currentTime);
            hearing.getAuditDetails().setLastModifiedBy(requestInfo.getUserInfo().getUuid());
            hearing.setRowVersion(hearing.getRowVersion() + 1);

        });

    }

    private AuditDetails getAuditDetailsScheduleHearing(RequestInfo requestInfo) {

        return AuditDetails.builder()
                .createdBy(requestInfo.getUserInfo().getUuid())
                .createdTime(System.currentTimeMillis())
                .lastModifiedBy(requestInfo.getUserInfo().getUuid())
                .lastModifiedTime(System.currentTimeMillis())
                .build();

    }
}

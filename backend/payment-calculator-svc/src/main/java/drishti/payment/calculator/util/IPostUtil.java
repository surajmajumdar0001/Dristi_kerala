package drishti.payment.calculator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import drishti.payment.calculator.config.ServiceConstants;
import drishti.payment.calculator.web.models.IPostConfigParams;
import net.minidev.json.JSONArray;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class IPostUtil {
    private final MdmsUtil mdmsUtil;

    private final ObjectMapper objectMapper;

    @Autowired
    public IPostUtil(MdmsUtil mdmsUtil, ObjectMapper objectMapper) {
        this.mdmsUtil = mdmsUtil;
        this.objectMapper = objectMapper;
    }

    public IPostConfigParams getIPostFeesDefaultData() {

        RequestInfo requestInfo = RequestInfo.builder().build(); //todo: change it with user request
        String tenantId = "kl";// todo: change with request tenant
        String module = ServiceConstants.SUMMON_MODULE;
        String master = ServiceConstants.I_POST_MASTER;
        Map<String, Map<String, JSONArray>> response = mdmsUtil.fetchMdmsData(requestInfo, tenantId, module, Collections.singletonList(master));

        //todo :add other methods
        return new IPostConfigParams();

    }
}

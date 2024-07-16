package org.pucar.dristi.service;

import com.jayway.jsonpath.JsonPath;
import digit.models.coremodels.RequestInfoWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.pucar.dristi.config.Configuration;
import org.pucar.dristi.kafka.Producer;
import org.pucar.dristi.repository.ServiceRequestRepository;
import org.pucar.dristi.web.models.CaseRequest;
import org.pucar.dristi.web.models.SMSRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.pucar.dristi.config.ServiceConstants.*;

@Service
@Slf4j
public class NotificationService {

    private final Configuration config;

    private final Producer producer;

    private final ServiceRequestRepository repository;

    @Autowired
    public NotificationService(Configuration config, Producer producer, ServiceRequestRepository repository) {
        this.config = config;
        this.producer = producer;
        this.repository = repository;
    }

    public void sendNotification(CaseRequest request) {
        String action = request.getCases().getWorkflow().getAction();
        String message = getMessageBasedOnAction(request, action);
        if (StringUtils.isEmpty(message)) {
            log.info("SMS content has not been configured for this case");
            return;
        } else {
            message = getMessage(request, HIGH_COURT_LOCALIZATION_CODE) + message;
        }
        pushNotification(request, message);
    }

    private void pushNotification(CaseRequest request, String message) {

        //get individual name, id, mobileNumber
        log.info("get case e filing number, id, cnr");
        Map<String, String> smsDetails = getDetailsForSMS(request);

        log.info("build Message");
        message = buildMessage(smsDetails, message);
        SMSRequest smsRequest = SMSRequest.builder()
                .mobileNumber(smsDetails.get("mobileNumber"))
                .tenantId(smsDetails.get("tenantId"))
                .templateId(config.getSmsNotificationTemplateId())
                .contentType("TEXT")
                .category("NOTIFICATION")
                .locale(NOTIFICATION_ENG_LOCALE_CODE)
                .expiryTime(System.currentTimeMillis() + 60 * 60 * 1000)
                .message(message).build();
        log.info("push message");
        producer.push(config.getSmsNotificationTopic(), smsRequest);
    }

    private Map<String, String> getDetailsForSMS(CaseRequest request) {
        Map<String, String> smsDetails = new HashMap<>();

        smsDetails.put("caseId", request.getCases().getCaseNumber());
        smsDetails.put("efilingNumber", request.getCases().getFilingNumber());
        smsDetails.put("cnr", request.getCases().getCnrNumber());
        smsDetails.put("date", request.getCases().getFilingNumber());
        smsDetails.put("tenantId", request.getCases().getTenantId().split("\\.")[0]);
        smsDetails.put("mobileNumber", request.getRequestInfo().getUserInfo().getMobileNumber());
        return smsDetails;
    }

    private String getMessageBasedOnAction(CaseRequest request, String action) {
        return switch (action.toUpperCase()) {
            case "SUBMIT_CASE" -> getMessage(request, "CASE_SUBMISSION");
            case "MAKE_PAYMENT" -> getMessage(request, "CASE_FILED");
            case "VALIDATE" -> getMessage(request, "SCRUTINY_COMPLETE_CASE_REGISTERED");
            case "SEND_BACK" -> getMessage(request, "EFILING_ERRORS");
            case "SCHEDULE_ADMISSION_HEARING" -> getMessage(request, "ADMISSION_HEARING_SCHEDULED");
            case "ADMIT" -> getMessage(request, "CASE_ADMITTED");
            case "REJECT" -> getMessage(request, "HEARING_REJECTED");
            default -> null;
        };
    }

    /**
     * Gets the message from localization
     *
     * @param request
     * @param msgCode
     * @return
     */

    public String getMessage(CaseRequest request, String msgCode) {
        String rootTenantId = request.getCases().getTenantId().split("\\.")[0];
        RequestInfo requestInfo = request.getRequestInfo();
        Map<String, Map<String, String>> localizedMessageMap = getLocalisedMessages(requestInfo, rootTenantId,
                NOTIFICATION_ENG_LOCALE_CODE, NOTIFICATION_MODULE_CODE);
        return localizedMessageMap.get(NOTIFICATION_ENG_LOCALE_CODE + "|" + rootTenantId).get(msgCode);
    }

    /**
     * Builds msg based on the format
     *
     * @param message
     * @param userDetailsForSMS
     * @return
     */
    public String buildMessage(Map<String, String> userDetailsForSMS, String message) {
        message = message.replace("{{caseId}}", userDetailsForSMS.get("caseId"))
                .replace("{Efiling number}", userDetailsForSMS.get("Efiling number"))
                .replace("{cnr}", userDetailsForSMS.get("cnr"))
                .replace("{date}", userDetailsForSMS.get("date"));
        return message;
    }

    /**
     * Creates a cache for localization that gets refreshed at every call.
     *
     * @param requestInfo
     * @param rootTenantId
     * @param locale
     * @param module
     * @return
     */
    public Map<String, Map<String, String>> getLocalisedMessages(RequestInfo requestInfo, String rootTenantId, String locale, String module) {
        Map<String, Map<String, String>> localizedMessageMap = new HashMap<>();
        Map<String, String> mapOfCodesAndMessages = new HashMap<>();
        StringBuilder uri = new StringBuilder();
        RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
        requestInfoWrapper.setRequestInfo(requestInfo);
        uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
                .append(config.getLocalizationSearchEndpoint()).append("?tenantId=" + rootTenantId)
                .append("&module=" + module).append("&locale=" + locale);
        List<String> codes = null;
        List<String> messages = null;
        Object result = null;
        try {
            result = repository.fetchResult(uri, requestInfoWrapper);
            codes = JsonPath.read(result, NOTIFICATION_LOCALIZATION_CODES_JSONPATH);
            messages = JsonPath.read(result, NOTIFICATION_LOCALIZATION_MSGS_JSONPATH);
        } catch (Exception e) {
            log.error("Exception while fetching from localization: " + e);
        }
        if (null != result) {
            for (int i = 0; i < codes.size(); i++) {
                mapOfCodesAndMessages.put(codes.get(i), messages.get(i));
            }
            localizedMessageMap.put(locale + "|" + rootTenantId, mapOfCodesAndMessages);
        }

        return localizedMessageMap;
    }
}

package org.egov.web.notification.mail.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.web.notification.mail.config.ApplicationConfiguration;
import org.egov.web.notification.mail.consumer.contract.Email;
import org.egov.web.notification.mail.utils.Constants;
import org.egov.web.notification.mail.utils.MdmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MessageConstruction {

    @Autowired
    private MdmsUtil mdmsUtil;

    @Autowired
    private ApplicationConfiguration config;


    public String constructMessage(Email email){
        String templateId = email.getTemplateId();
        String moduleName = Constants.EMAIL_TEMPLATE_MODULE_NAME;
        List<String> masterName = new ArrayList<>();
        masterName.add(templateId);

        RequestInfo requestInfo = new RequestInfo();

        Map<String, Map<String, JSONArray>> mdmsData = mdmsUtil.fetchMdmsData(requestInfo, config.getStateTenantId(), moduleName, masterName);

        JSONArray templateArray = mdmsData.get(moduleName).get(templateId);
        if(templateArray == null || templateArray.isEmpty()){
            throw new RuntimeException("Template not found");
        }

        LinkedHashMap templateMap = (LinkedHashMap) templateArray.get(0);
        String handlerTemplate = String.valueOf(templateMap.get("value"));

        Handlebars handlebars = new Handlebars();
        Template template = null;
        try {
            template = handlebars.compileInline(handlerTemplate);
            Map<String, String> data = new HashMap<>();

            String emailBody = email.getBody();
            JsonObject jsonObject = JsonParser.parseString(emailBody).getAsJsonObject();

            for(String key: jsonObject.keySet()){
                data.put(key, jsonObject.get(key).getAsString());
            }

            return template.apply(data);
        } catch (Exception e) {
            throw new CustomException("HANDLEBARS_ERROR", "Error while compiling handlebars template");
        }
    }
}

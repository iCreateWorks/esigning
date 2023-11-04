package com.icw.esign.service.template;

import com.icw.esign.documents.Template;
import com.icw.esign.service.cloud.CloudFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateService {

    @Autowired
    CloudFileStorage cloudFileStorage;

    private String pattern = "\\{\\{([\\w]+)\\}\\}";

    public List<Template> getTemplates(String documentCode) {
        try {
            List<Template> templates = new ArrayList<>();
            Template template = new Template();
            String documentPath = "templates/" + documentCode + ".html";
            byte[] fileContent = this.cloudFileStorage.downloadFile(documentPath);
            String templateContent = new String(fileContent);
            template.setMessage(templateContent);
            template.setChannel("DOCUMENT");
            templates.add(template);
            return templates;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public String getMessage(Template template, Map<String, String> fields, String localePreference) {
        String templateMessage = this.getTemplateMessageBasedOnLocale(template, localePreference);
        final String message = this.replaceTokens(templateMessage, fields);
        return message;
    }

    private String getTemplateMessageBasedOnLocale(Template template, String localePreference){
        String templateMessage = null;
        if (localePreference != null){
            if (localePreference.equalsIgnoreCase("es-US")){
                templateMessage = StringUtils.hasText(template.getMessage_es()) ? template.getMessage_es() : template.getMessage();
            } else {
                templateMessage = template.getMessage();
            }
        } else{
            templateMessage = template.getMessage();
        }
        return templateMessage;
    }

    public String replaceTokens(String messageTemplate, Map<String, String> normalizedFieldMap) {
        StringBuffer messageBuffer = new StringBuffer();
        if (messageTemplate != null) {
            Pattern pattern = Pattern.compile(this.pattern, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(messageTemplate);
            while (matcher.find()) {
                String messageToken = matcher.group();
                String fieldName = messageToken.substring(2, messageToken.length() - 2);
                String fieldValue = normalizedFieldMap.get(fieldName);
                if (fieldValue != null) {
                    if (fieldValue.contains("$")) {
                        fieldValue = fieldValue.replace("$", "\\$");
                    }
                    matcher.appendReplacement(messageBuffer, fieldValue);
                } else {
                    matcher.appendReplacement(messageBuffer, "");
                }
            }

            matcher.appendTail(messageBuffer);
        }

        return messageBuffer.toString();
    }
}

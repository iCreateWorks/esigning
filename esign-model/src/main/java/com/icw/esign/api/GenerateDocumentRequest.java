package com.icw.esign.api;

import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

public class GenerateDocumentRequest {
    @NotEmpty(message = "Document code is required")
    String docCode;
    @NotEmpty(message = "Document type is required")
    String docType;
    @NotEmpty(message = "Context Id is required")
    String contextId;
    @NotEmpty(message = "Content type is required")
    String contentType;
    String localePreference;
    @NotEmpty(message = "Template fields are required")
    private Map<String, String> fields;
    private boolean encryptDocument;

    private String timezone;

    public String getDocCode() {
        return docCode;
    }

    public void setDocCode(String docCode) {
        this.docCode = docCode;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public boolean isEncryptDocument() {
        return encryptDocument;
    }

    public void setEncryptDocument(boolean encryptDocument) {
        this.encryptDocument = encryptDocument;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLocalePreference() {
        return localePreference;
    }

    public void setLocalePreference(String localePreference) {
        this.localePreference = localePreference;
    }
}


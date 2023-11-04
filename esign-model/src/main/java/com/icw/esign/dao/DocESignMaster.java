package com.icw.esign.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;

@Entity
public class DocESignMaster {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "doc_uuid")
    private String docUUID;

    @Column(name = "context_id")
    private String contextId;

    @Column(name = "doc_type")
    private String docType;

    @Column(name = "doc_path")
    private String docPath;

    @Column(name = "pdf_doc_path")
    private String pdfDocPath;

    @Column(name = "doc_template_code")
    private String docCode;

    @Column(name = "encrypted")
    private boolean encrypted;

    @Column(name="upload_date")
    private Date uploadedDate;

    @Lob
    @Column(name="doc_content")
    private String content;

    @Column(name = "doc_sign_date")
    private Date docSignDate;

    @Column(name = "time_zone_id")
    private String timezone;

    @Column(name = "locale_pref")
    private String localePref;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocUUID() {
        return docUUID;
    }

    public void setDocUUID(String docUUID) {
        this.docUUID = docUUID;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocPath() {
        return docPath;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    public String getPdfDocPath() {
        return pdfDocPath;
    }

    public void setPdfDocPath(String pdfDocPath) {
        this.pdfDocPath = pdfDocPath;
    }

    public String getDocCode() {
        return docCode;
    }

    public void setDocCode(String docCode) {
        this.docCode = docCode;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(Date uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDocSignDate() {
        return docSignDate;
    }

    public void setDocSignDate(Date docSignDate) {
        this.docSignDate = docSignDate;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLocalePref() {
        return localePref;
    }

    public void setLocalePref(String localePref) {
        this.localePref = localePref;
    }
}

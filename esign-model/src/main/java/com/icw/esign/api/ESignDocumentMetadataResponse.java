package com.icw.esign.api;

import java.util.Date;

public class ESignDocumentMetadataResponse {

    private String htmlDocPath;
    private String pdfDocPath;

    private Date signedDateTime;

    public String getHtmlDocPath() {
        return htmlDocPath;
    }

    public void setHtmlDocPath(String htmlDocPath) {
        this.htmlDocPath = htmlDocPath;
    }

    public String getPdfDocPath() {
        return pdfDocPath;
    }

    public void setPdfDocPath(String pdfDocPath) {
        this.pdfDocPath = pdfDocPath;
    }

    public Date getSignedDateTime() {
        return signedDateTime;
    }

    public void setSignedDateTime(Date signedDateTime) {
        this.signedDateTime = signedDateTime;
    }
}


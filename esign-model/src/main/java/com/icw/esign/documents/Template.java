package com.icw.esign.documents;

public class Template {

    private Integer restrictedDeliveryTime;
    private String deliveryTimeStart;
    private String deliveryTimeEnd;
    private String persistFields;
    private String channel;
    private String title;
    private String message;
    private String linkName;
    private boolean contingent;
    private boolean templateActive;
    private String displayCategory;
    private String deliveryTimeZone;
    private String title_es;
    private String message_es;

    private boolean forceGenerate;

    public Integer getRestrictedDeliveryTime() {
        return restrictedDeliveryTime;
    }

    public void setRestrictedDeliveryTime(Integer restrictedDeliveryTime) {
        this.restrictedDeliveryTime = restrictedDeliveryTime;
    }

    public String getDeliveryTimeStart() {
        return deliveryTimeStart;
    }

    public void setDeliveryTimeStart(String deliveryTimeStart) {
        this.deliveryTimeStart = deliveryTimeStart;
    }

    public String getDeliveryTimeEnd() {
        return deliveryTimeEnd;
    }

    public void setDeliveryTimeEnd(String deliveryTimeEnd) {
        this.deliveryTimeEnd = deliveryTimeEnd;
    }

    public String getPersistFields() {
        return persistFields;
    }

    public void setPersistFields(String persistFields) {
        this.persistFields = persistFields;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public boolean isContingent() {
        return contingent;
    }

    public void setContingent(boolean contingent) {
        this.contingent = contingent;
    }

    public boolean isTemplateActive() {
        return templateActive;
    }

    public void setTemplateActive(boolean templateActive) {
        this.templateActive = templateActive;
    }

    public String getDisplayCategory() {
        return displayCategory;
    }

    public void setDisplayCategory(String displayCategory) {
        this.displayCategory = displayCategory;
    }

    public String getDeliveryTimeZone() {
        return deliveryTimeZone;
    }

    public void setDeliveryTimeZone(String deliveryTimeZone) {
        this.deliveryTimeZone = deliveryTimeZone;
    }

    public String getTitle_es() {
        return title_es;
    }

    public void setTitle_es(String title_es) {
        this.title_es = title_es;
    }

    public String getMessage_es() {
        return message_es;
    }

    public void setMessage_es(String message_es) {
        this.message_es = message_es;
    }

    public boolean isForceGenerate() {
        return forceGenerate;
    }

    public void setForceGenerate(boolean forceGenerate) {
        this.forceGenerate = forceGenerate;
    }

    public boolean isDocument() {
        return this.channel.equalsIgnoreCase("DOCUMENT");
    }
}

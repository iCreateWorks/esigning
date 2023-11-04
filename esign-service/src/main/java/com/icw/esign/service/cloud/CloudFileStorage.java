package com.icw.esign.service.cloud;

public interface CloudFileStorage {
    String uploadFile(String filePath, byte[] fileContent, String contentType);
    byte[] downloadFile(String filePath);
    void deleteFile(String filePath);
    String getLimitedTimePreSignedUrl(String documentPath, String s3BucketName);
}

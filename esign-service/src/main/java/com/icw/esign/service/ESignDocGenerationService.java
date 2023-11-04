package com.icw.esign.service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ESignDocGenerationService {

    Map<String, String> generateDocument(String contextId,
                                         String docCode,
                                         String docType,
                                         Map<String, String> fields,
                                         String timezone,
                                         String localePreference);

    Map<String, String> embedSignatureFile(String accessToken,
                                                  MultipartFile imageFile,
                                                  String docUUID) throws IOException;

    byte[] retrieveDocument(String accessToken, String contentType , String docUuid);

    void storeDeviceInfo(String userAgent, String clientIpAddress, int docId);
}

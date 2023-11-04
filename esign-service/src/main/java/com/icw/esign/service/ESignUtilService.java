package com.icw.esign.service;

import com.icw.esign.dao.DocESignMaster;
import com.icw.esign.exception.EDeliveryException;
import org.json.JSONException;

import java.io.IOException;

public interface ESignUtilService {
    DocESignMaster getDocumentTrackerInfo(String docType, String docUuid, boolean equalityCheck);

    boolean isValidDocUUID(String docUUID);

    byte[] getDocumentFromS3(String accessToken, String documentPath);

    String uploadFile(String contextId, String docCode, String docKey, String fileName, byte[] docBytes, String contentType);
}

package com.icw.esign.service;
import com.icw.esign.dao.DocESignMaster;
import com.icw.esign.repository.ESignDbRepository;
import com.icw.esign.service.cloud.CloudFileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ESignUtilServiceImpl implements ESignUtilService {

    private static final Logger logger = LoggerFactory.getLogger(ESignUtilServiceImpl.class);

    @Value("${s3.bucket.name}")
    private String s3BucketName;
    @Autowired
    ESignDbRepository eSignDbRepository;

    @Autowired
    CloudFileStorage cloudFileStorage;

    @Override
    public DocESignMaster getDocumentTrackerInfo(String docType, String docUuid, boolean equalityCheck) {
        List<DocESignMaster> docESignTrackers = eSignDbRepository.getDocumentESignInfo(docUuid);
        Optional<DocESignMaster> optionalDocESignTracker;
        if (equalityCheck) {
            optionalDocESignTracker = docESignTrackers.stream()
                    .filter(docESignTracker -> docType.equalsIgnoreCase(docESignTracker.getDocType())).findFirst();
        } else {
            optionalDocESignTracker = docESignTrackers.stream()
                    .filter(docESignTracker -> !docType.equalsIgnoreCase(docESignTracker.getDocType())).findFirst();
        }
        if (optionalDocESignTracker.isPresent()) {
            return optionalDocESignTracker.get();
        }
        return null;
    }

    @Override
    public boolean isValidDocUUID(String docUuid) {
        return !CollectionUtils.isEmpty(eSignDbRepository.getDocumentESignInfo(docUuid));
    }

    @Override
    public byte[] getDocumentFromS3(String accessToken, String documentPath) {
        return this.cloudFileStorage.downloadFile(documentPath);
    }

    @Override
    public String uploadFile(String contextId, String docCode,
                             String docKey, String fileName, byte[] docBytes, String contentType)  {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(Date.from(Instant.now()));
            docKey = (docKey != null) ? docKey : String.format("Loan/%s/%s.%s", contextId, today, fileName);
            return this.cloudFileStorage.uploadFile(docKey, docBytes, contentType);
        } catch (Exception ex) {
            logger.error(String.format("Error uploading to Document Service. notifCode=%s, contextId=%s", docCode, contextId), ex);
        }
        return null;
    }

}

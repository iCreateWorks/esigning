package com.icw.esign.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icw.esign.dao.DocESignMaster;
import com.icw.esign.device.UserAgentDeviceInfo;
import com.icw.esign.documents.Template;
import com.icw.esign.service.cloud.CloudFileStorage;
import com.icw.esign.service.device.DeviceInfoService;
import com.icw.esign.service.template.TemplateService;
import com.icw.esign.repository.ESignDbRepository;
import com.icw.esign.service.chrome.ChromeHeadless;
import com.icw.esign.utils.EDelUtility;
import com.icw.esign.utils.Pair;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ESignDocGenerationServiceImpl implements ESignDocGenerationService {

    @Autowired
    TemplateService templateService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ESignUtilService eSignUtilService;

    @Autowired
    ChromeHeadless chromeHeadless;

    @Autowired
    ESignDbRepository utilDbRepository;

    @Autowired
    SignAndLockPdfService signAndLockPdfService;

    @Value("${edelivery.prefix}")
    String edeliveryApiPrefix;

    @Value("${s3.bucket.name}")
    String s3Bucket;

    @Autowired
    DeviceInfoService deviceInfoService;

    @Autowired
    CloudFileStorage cloudFileStorage;

    private String pattern = "\\{\\{([\\w]+)\\}\\}";
    private static final Logger logger = LoggerFactory.getLogger(ESignDocGenerationServiceImpl.class);

    @Override
    public Map<String, String> generateDocument(String contextId,
                                                String docCode,
                                                String docType,
                                                Map<String, String> fields,
                                                String timezone,
                                                String localePreference)  {
        Map<String, String> returnMap = new HashMap<>();
        //eDeliveryService.setAuthToken(authToken);
        List<Template> templates = this.templateService.getTemplates(docCode);
        Optional<Template> docTemplate = templates.stream().filter(Template::isDocument).findFirst();
        String html = null;
        if (StringUtils.isBlank(localePreference)) {
            localePreference = "en-US";
        }
        if (docTemplate.isPresent()) {
            final Template template = docTemplate.get();
            logger.info("Document template found for docCode {} and ContextId={}.", docCode, contextId);
            html = this.templateService.getMessage(template, fields, localePreference);
            logger.info("HTML document generated successfully for docCode={} and ContextId={}", docCode, contextId);
        } else {
            logger.warn("No document template found for docCode {}. ContextId={}", docCode, contextId);
        }
        Pair htmlDocKeyAndFileName = EDelUtility.getDocKeyAndFileName(contextId, docCode, docType, "html", "text/html");
        String htmlDocId = eSignUtilService.uploadFile(contextId, docCode, htmlDocKeyAndFileName.getKey(),
                htmlDocKeyAndFileName.getValue(), html.getBytes(), htmlDocKeyAndFileName.getContentType());
        returnMap.put("html", htmlDocId);
        logger.info("Html version of the document saved to {}", htmlDocId);

        // Generate PDF
        byte[] pdfBytes = generatePDFFromHTML(html);
        Pair pdfDocKeyAndFileName = EDelUtility.getDocKeyAndFileName(contextId, docCode, docType,"pdf", "application/pdf");
        String pdfDocId = eSignUtilService.uploadFile(contextId, docCode, pdfDocKeyAndFileName.getKey(),
                pdfDocKeyAndFileName.getValue(), pdfBytes, pdfDocKeyAndFileName.getContentType());
        returnMap.put("pdf", pdfDocId);

        UUID uuid = UUID.randomUUID();
        int docId = utilDbRepository.saveESignDocumentInfo(uuid.toString(), contextId, docType,
               htmlDocId, pdfDocId, docCode,  null, fields, StringUtils.isBlank(timezone) ? "America/New_York" : timezone, localePreference);
        returnMap.put("docUUID", uuid.toString());
        returnMap.put("docId", String.valueOf(docId));
        return returnMap;
    }

    @Override
    public Map<String, String> embedSignatureFile(String accessToken,
                                                  MultipartFile imageFile, String docUUID) throws IOException {
        Map<String, String> returnMap = new HashMap<>();
        String docType = "CustomerSignature";
        String docCode = "customer_signature";

        String extension = EDelUtility.getFileExtension(imageFile);
        DocESignMaster documentTrackerInfo = eSignUtilService.getDocumentTrackerInfo(docType, docUUID, false);

        if (Objects.nonNull(documentTrackerInfo)) {
            Pair imageDocKeyAndFileName  = EDelUtility.getDocKeyAndFileName(documentTrackerInfo.getContextId(), docCode, docType, extension, imageFile.getContentType());
            String docId = eSignUtilService.uploadFile(documentTrackerInfo.getContextId(),
                    docCode, imageDocKeyAndFileName.getKey(), imageDocKeyAndFileName.getValue(), imageFile.getBytes(), imageDocKeyAndFileName.getContentType());
            utilDbRepository.saveESignDocumentInfo(docUUID, documentTrackerInfo.getContextId(), docType, docId, null, docCode,
                    null, null, null, null);
            String secureSignatureImageUrl = this.cloudFileStorage.getLimitedTimePreSignedUrl(docId, this.s3Bucket);
            logger.info("Secure URL : {}",secureSignatureImageUrl);
            String fieldContent = documentTrackerInfo.getContent();
            Map <String, String> contentMap = objectMapper.readValue(fieldContent, new TypeReference<HashMap<String, String>>() {});
            String signedDate = EDelUtility.getDocumentTimeWithZone(StringUtils.isBlank(documentTrackerInfo.getTimezone()) ? "America/New_York" : documentTrackerInfo.getTimezone());
            contentMap.put("SignedDateTime", signedDate);
            contentMap.put("CustomerSignature", secureSignatureImageUrl);
            contentMap.put("DisplaySignedNameAndDate", "");
            Template template = getTemplate(documentTrackerInfo.getDocCode(), accessToken);
            String signedHtml = this.templateService.getMessage(template, contentMap, documentTrackerInfo.getLocalePref());
            Pair htmlDocKeyAndFileName = EDelUtility.getDocKeyAndFileName(documentTrackerInfo.getContextId(), documentTrackerInfo.getDocCode(), documentTrackerInfo.getDocType(), "html", "text/html");
            String htmlDocId = eSignUtilService.uploadFile(documentTrackerInfo.getContextId(),
                    documentTrackerInfo.getDocCode(), htmlDocKeyAndFileName.getKey(), htmlDocKeyAndFileName.getValue(), signedHtml.getBytes(), htmlDocKeyAndFileName.getContentType());
            returnMap.put("html", htmlDocId);
            logger.info("Html version of the document saved to {}", htmlDocId);

            // Generate PDF
            byte[] pdfBytes = generatePDFFromHTML(signedHtml);

            byte[]  signedPdfBytes = new byte[0];
            try {
                logger.info("[Start] Sign and lock PDF");
                signedPdfBytes = signAndLockPdfService.encryptAndSignPdf(pdfBytes);
                logger.info("[Completed] Sign and lock PDF");
            } catch (Exception e) {
                logger.error("[Exception] Sign and lock PDF : {}", e.getMessage());
            }
            Pair pdfDocKeyAndFileName = EDelUtility.getDocKeyAndFileName(documentTrackerInfo.getContextId(),
                    documentTrackerInfo.getDocCode(), documentTrackerInfo.getDocType(),"pdf", "application/pdf");
            String pdfDocId = eSignUtilService.uploadFile(documentTrackerInfo.getContextId(), documentTrackerInfo.getDocCode(), pdfDocKeyAndFileName.getKey(), pdfDocKeyAndFileName.getValue(), signedPdfBytes, pdfDocKeyAndFileName.getContentType());
            returnMap.put("pdf", pdfDocId);

            int dbDocId = utilDbRepository.saveESignDocumentInfo(docUUID, documentTrackerInfo.getContextId(), documentTrackerInfo.getDocType(), htmlDocId, pdfDocId, documentTrackerInfo.getDocCode(),
                    EDelUtility.getLocalDateFromString(signedDate), contentMap, documentTrackerInfo.getTimezone(), documentTrackerInfo.getLocalePref());
            returnMap.put("docUUID", docUUID);
            returnMap.put("docId", String.valueOf(dbDocId));
            return returnMap;

        }
        return returnMap;
    }

    @Override
    public byte[] retrieveDocument(String accessToken, String contentType , String docUuid) {
        DocESignMaster docESignTrackerInfo =  eSignUtilService.getDocumentTrackerInfo("CustomerSignature", docUuid, false);
        if (Objects.nonNull(docESignTrackerInfo)) {
            String path = "html".equalsIgnoreCase(contentType) ? docESignTrackerInfo.getDocPath(): docESignTrackerInfo.getPdfDocPath();
            return eSignUtilService.getDocumentFromS3(accessToken, path);
        }
       return null;
    }

    @Override
    public void storeDeviceInfo(String userAgent, String clientIpAddress, int docId) {
        UserAgentDeviceInfo userAgentDeviceInfo = deviceInfoService.getDeviceInfo(userAgent);
        utilDbRepository.saveESignDeviceInfo(docId, userAgentDeviceInfo, clientIpAddress);
    }

    private Template getTemplate(String docCode, String accessToken)  {
        List<Template> templates = templateService.getTemplates(docCode);
        Optional<Template> docTemplate = templates.stream().filter(t -> t.isDocument()).findFirst();
        return docTemplate.get();
    }

    private byte[] generatePDFFromHTML(String message) {
        try {
            return chromeHeadless.getPDF(message, false);
        } catch (Exception ex) {
            logger.error("Failed to generate PDF", ex);
        }
        return null;
    }
}
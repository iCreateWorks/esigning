package com.icw.esign.controller;

import com.icw.esign.api.ESignDocumentMetadataResponse;
import com.icw.esign.api.GenerateDocumentRequest;
import com.icw.esign.dao.DocESignMaster;
import com.icw.esign.service.ESignDocGenerationService;
import com.icw.esign.service.ESignUtilService;
import com.icw.esign.service.device.DeviceInfoService;
import com.icw.esign.utils.Constants;
import com.icw.esign.utils.EDelUtility;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.Map;

@RestController
public class ESignController {

    private final static Logger logger = LoggerFactory.getLogger(ESignController.class);

    @Autowired
    ESignDocGenerationService eSignDocGenerationService;

    @Autowired
    ESignUtilService eSignUtilService;

    @Autowired
    DeviceInfoService deviceInfoService;

    @PostMapping(path = {"document/{contentType}"}, produces = {"application/json"})
    public ResponseEntity<byte[]> generateDocument(@RequestHeader HttpHeaders headers,
                                                   HttpServletRequest httpServletRequest,
                                                   @PathVariable @NotEmpty(message = "Doc UUID is required") String contentType,
                                                   @Valid @RequestBody GenerateDocumentRequest generateDocumentRequest) {
        Map<String, String> docKeys = eSignDocGenerationService.generateDocument(
                generateDocumentRequest.getContextId(),
                generateDocumentRequest.getDocCode(),
                generateDocumentRequest.getDocType(),
                generateDocumentRequest.getFields(),
                generateDocumentRequest.getTimezone(),
                generateDocumentRequest.getLocalePreference());

        HttpHeaders responseHeaders = new HttpHeaders();
        byte[] response;
        String fileName = null;
        if ("html".equalsIgnoreCase(generateDocumentRequest.getContentType())) {
            response =  eSignUtilService.getDocumentFromS3(this.getAuthToken(headers), docKeys.get("html"));
            responseHeaders.setContentType(MediaType.TEXT_HTML);
            fileName = "unsigned_document.html";
        } else {
            response =  eSignUtilService.getDocumentFromS3(this.getAuthToken(headers), docKeys.get("pdf"));
            responseHeaders.setContentType(MediaType.APPLICATION_PDF);
            fileName = "unsigned_document.pdf";
        }
        responseHeaders.add("htmlDocId", docKeys.get("html"));
        responseHeaders.add("pdfDocId", docKeys.get("pdf"));
        responseHeaders.add("docUUID", docKeys.get("docUUID"));
        headers.setContentDispositionFormData(fileName, fileName);
        return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
    }

    @PostMapping(path = {"document/signature/{docUUID}/{contentType}"})
    public ResponseEntity<?> signAndReturnDocument(@RequestHeader HttpHeaders headers,
                                                   HttpServletRequest httpServletRequest,
                                                   @RequestParam("signature") MultipartFile signatureImageFile,
                                                   @PathVariable @NotEmpty(message = "Doc UUID is required") String docUUID,
                                                   @PathVariable @NotEmpty(message = "Content type is required") String contentType) throws IOException, CryptoException {

        Map<String, String> docKeys = eSignDocGenerationService.embedSignatureFile(getAuthToken(headers), signatureImageFile, docUUID);
        String clientIpAddress = deviceInfoService.getClientIpAddress(httpServletRequest, headers);
        String userAgentHeader = StringUtils.isNotBlank(headers.getFirst(Constants.API_USER_AGENT)) ?
                headers.getFirst(Constants.API_USER_AGENT) : headers.getFirst(Constants.BROWSER_USER_AGENT);
        String userAgent = EDelUtility.getUserAgentToken(userAgentHeader);
        eSignDocGenerationService.storeDeviceInfo(userAgent, clientIpAddress, Integer.parseInt(docKeys.get("docId")));
        HttpHeaders responseHeaders = new HttpHeaders();
        byte[] response;
        String fileName = null;
        if ("html".equalsIgnoreCase(contentType)) {
            response =  eSignUtilService.getDocumentFromS3(this.getAuthToken(headers), docKeys.get("html"));
            responseHeaders.setContentType(MediaType.TEXT_HTML);
            fileName = "signed_document.html";
        } else {
            response =  eSignUtilService.getDocumentFromS3(this.getAuthToken(headers), docKeys.get("pdf"));
            responseHeaders.setContentType(MediaType.APPLICATION_PDF);
            fileName = "signed_document.pdf";
        }
        responseHeaders.add("htmlDocId", docKeys.get("html"));
        responseHeaders.add("pdfDocId", docKeys.get("pdf"));
        responseHeaders.add("docUUID", docKeys.get("docUUID"));
        headers.setContentDispositionFormData(fileName, fileName);
        return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
    }

    @GetMapping(path = {"document/{docUUID}/{contentType}"})
    public ResponseEntity<?> getDocument(@RequestHeader HttpHeaders headers,
                                         @PathVariable @NotEmpty(message = "Doc UUID is required") String docUUID,
                                         @PathVariable @NotEmpty(message = "Content type is required") String contentType) {
        byte[] response = eSignDocGenerationService.retrieveDocument(this.getAuthToken(headers), contentType, docUUID);
        HttpHeaders responseHeaders = new HttpHeaders();
        if ("html".equalsIgnoreCase(contentType)) {
            responseHeaders.setContentType(MediaType.TEXT_HTML);
        } else {
            responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        }
        return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
    }

    @GetMapping(path = {"document/retrieve/metadata/{docUUID}"})
    public ResponseEntity<?> getDocumentMetadata(@RequestHeader HttpHeaders headers,
                                                 @PathVariable @NotEmpty(message = "Doc UUID is required") String docUUID) {
        DocESignMaster docESignTrackerInfo =  eSignUtilService.getDocumentTrackerInfo("CustomerSignature", docUUID, false);
        ESignDocumentMetadataResponse eSignDocumentMetadataResponse = new ESignDocumentMetadataResponse();
        eSignDocumentMetadataResponse.setHtmlDocPath(docESignTrackerInfo.getDocPath());
        eSignDocumentMetadataResponse.setPdfDocPath(docESignTrackerInfo.getPdfDocPath());
        eSignDocumentMetadataResponse.setSignedDateTime(docESignTrackerInfo.getDocSignDate());
        return new ResponseEntity<>(eSignDocumentMetadataResponse, HttpStatus.OK);
    }

    private String getAuthToken(HttpHeaders headers) {
        return headers.getFirst("authorization");
    }
}

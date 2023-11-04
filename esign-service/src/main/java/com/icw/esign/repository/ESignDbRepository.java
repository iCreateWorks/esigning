package com.icw.esign.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icw.esign.dao.DocESignMaster;
import com.icw.esign.device.UserAgentDeviceInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceException;
import javax.persistence.StoredProcedureQuery;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Repository
public class ESignDbRepository {
    private static final Logger logger = LoggerFactory.getLogger(ESignDbRepository.class);
    @Qualifier("eSignEntityManagerFactory")
    @Autowired
    private EntityManager entityManager;

    @Autowired
    ObjectMapper objectMapper;

    String P_GET_DOC_ESIGN_INFO = "p_get_doc_esign_info";

    String P_UPDATE_DOC_ESIGN_INFO = "p_update_doc_esign_info";

    String P_CREATE_DOC_ESIGN_DEVICE_INFO = "p_create_doc_esign_device_info";

    public List<DocESignMaster> getDocumentESignInfo(String docUUID) {
        StoredProcedureQuery proc = entityManager.createStoredProcedureQuery(P_GET_DOC_ESIGN_INFO, DocESignMaster.class);
        proc.registerStoredProcedureParameter("v_doc_uuid", String.class, ParameterMode.IN);
        proc.setParameter("v_doc_uuid", docUUID);
        try {
            return (List<DocESignMaster>) proc.getResultList();
        } catch (PersistenceException ex) {
            logger.error("Error while fetching document eSign info for docUUID: {}", docUUID, ex);
        }
        return Collections.emptyList();
    }

    public int saveESignDocumentInfo(String docUUID,
                                     String contextId,
                                     String docType,
                                     String docPath,
                                     String pdfDocPath,
                                     String docCode,
                                     LocalDateTime signedDate,
                                     Map<String, String> fields,
                                     String timezone,
                                     String localPreference) {
        // write unit test for this method
        int id = 0;
        StoredProcedureQuery proc = entityManager.createStoredProcedureQuery(P_UPDATE_DOC_ESIGN_INFO);

        proc.registerStoredProcedureParameter("v_doc_uuid", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("v_context_id", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("v_doc_type", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("v_doc_path", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("v_pdf_doc_path", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("v_doc_code", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("v_content", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("v_signed_date", LocalDateTime.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("v_time_zone_id", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("v_locale_pref", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("o_id", int.class, ParameterMode.OUT);

        proc.setParameter("v_doc_uuid", docUUID);
        proc.setParameter("v_context_id", contextId);
        proc.setParameter("v_doc_type", docType);
        proc.setParameter("v_doc_path", docPath);
        proc.setParameter("v_pdf_doc_path", pdfDocPath);
        proc.setParameter("v_doc_code", docCode);
        proc.setParameter("v_signed_date", signedDate);
        proc.setParameter("v_time_zone_id", timezone);
        proc.setParameter("v_locale_pref", localPreference);

        if (!CollectionUtils.isEmpty(fields)) {
            try {
                String jsonFieldMap = objectMapper.writeValueAsString(fields);
                proc.setParameter("v_content", jsonFieldMap);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            proc.execute();
            id = ((Integer)proc.getOutputParameterValue("o_id"));
        } catch (PersistenceException ex) {
            logger.error("Error while saving document eSign info for docUUID: {}", docUUID, ex);
        }
        return id;
    }

    public void saveESignDeviceInfo(int docId,
                                    UserAgentDeviceInfo userAgentDeviceInfo ,
                                    String clientIpAddress) {

        StoredProcedureQuery proc = entityManager.createStoredProcedureQuery(P_CREATE_DOC_ESIGN_DEVICE_INFO);

        proc.registerStoredProcedureParameter("i_device_id", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("i_doc_id", Integer.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("i_platform", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("i_manufacturer", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("i_model", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("i_os_version", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("i_ip_address", String.class, ParameterMode.IN);

        proc.setParameter("i_doc_id", docId);
        proc.setParameter("i_device_id",
                StringUtils.isNotBlank(userAgentDeviceInfo.getDeviceId()) ? userAgentDeviceInfo.getDeviceId() : "Browser");
        proc.setParameter("i_platform", userAgentDeviceInfo.getPlatform());
        proc.setParameter("i_manufacturer", userAgentDeviceInfo.getManufacturer());
        proc.setParameter("i_model", userAgentDeviceInfo.getModel());
        proc.setParameter("i_os_version", userAgentDeviceInfo.getOsVersion());
        proc.setParameter("i_ip_address", clientIpAddress);

        try {
            proc.execute();
        } catch (PersistenceException ex) {
            logger.error(ex.getLocalizedMessage());
        }
    }
}

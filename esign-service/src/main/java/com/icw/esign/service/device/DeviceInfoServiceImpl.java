package com.icw.esign.service.device;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icw.esign.device.UserAgentDeviceInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Qualifier("deviceInfoService")
public class DeviceInfoServiceImpl implements DeviceInfoService {
    private static final Logger logger = LoggerFactory.getLogger(DeviceInfoServiceImpl.class);

    @Override
    public String getClientIpAddress(HttpServletRequest httpServletRequest, HttpHeaders headers){
        String clientIpAddress = null;
        try {
            if (httpServletRequest != null && httpServletRequest.getRemoteAddr() != null) {
                String remoteIPAddress = httpServletRequest.getRemoteAddr();
                logger.info("DeviceInfoServiceImpl::getClientIpAddress - Loan Quote, remote IP address received through httpServletRequest.getRemoteAddr():" + remoteIPAddress);
                clientIpAddress = remoteIPAddress;
            }
            if (headers != null) {
                ObjectMapper mapper = new ObjectMapper();
                String headerPayload = mapper.writeValueAsString(headers);
                logger.info("DeviceInfoServiceImpl::getClientIpAddress - Headers Payload Received :" + headerPayload);
                List<String> clientIpAddressList = headers.get("x-original-forwarded-for");
                if (clientIpAddressList != null && clientIpAddressList.size() > 0 && clientIpAddressList.get(0) != null) {
                    String ipAddressList = clientIpAddressList.get(0).trim().replace(" ", "");
                    String[] arr = ipAddressList.split(",");
                    String finalClientIpAddress = null;
                    if (arr.length > 0 && arr[0] != null) {
                        finalClientIpAddress = arr[0].trim();
                    }
                    logger.info("DeviceInfoServiceImpl::getClientIpAddress - Loan Quote, final remote client IP address received (finalClientIpAddress):" + finalClientIpAddress);
                    clientIpAddress = finalClientIpAddress;
                }
            }else{
                logger.error("DeviceInfoServiceImpl::getClientIpAddress - Headers are null");
            }
        }
        catch(Exception ex){
            logger.error("DeviceInfoServiceImpl::getClientIpAddress - An error occurred while fetching the client IP address, detail error:", ex);
        }
        return clientIpAddress;
    }

    @Override
    public UserAgentDeviceInfo getDeviceInfo(String userAgent) {
        UserAgentDeviceInfo userAgentDeviceInfo = new UserAgentDeviceInfo();
        try {
            if (StringUtils.isNotBlank(userAgent)) {
                String[] userAgentParts = userAgent.split("/");
                if (userAgentParts.length >= 5) {
                    String[] appInfo = userAgentParts[5].split(":");
                    if (appInfo.length >= 1) {
                        userAgentDeviceInfo.setAppName(appInfo[0]);
                        userAgentDeviceInfo.setAppVersion(appInfo[1]);
                    }
                    userAgentDeviceInfo.setDeviceId(userAgentParts[0]);
                    userAgentDeviceInfo.setPlatform(userAgentParts[1]);
                    userAgentDeviceInfo.setManufacturer(userAgentParts[2]);
                    userAgentDeviceInfo.setModel(userAgentParts[3]);
                    userAgentDeviceInfo.setOsVersion(userAgentParts[4]);
                }
            }
        }
        catch (Exception ex){
            logger.error("DeviceInfoServiceImpl::getDeviceId - An error occurred while fetching device Info, detail error:", ex);
        }
        return userAgentDeviceInfo;
    }

}

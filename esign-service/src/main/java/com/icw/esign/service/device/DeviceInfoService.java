package com.icw.esign.service.device;

import com.icw.esign.device.UserAgentDeviceInfo;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

public interface DeviceInfoService {
    String getClientIpAddress(HttpServletRequest httpServletRequest, HttpHeaders headers);
    UserAgentDeviceInfo getDeviceInfo(String userAgent);
}

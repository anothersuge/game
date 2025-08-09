package com.lvluolang.game.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for getting client IP address
 */
/**
 * 客户端IP地址工具类
 */
@Slf4j
public class ClientIpUtil {
    
    /**
     * 从HttpServletRequest获取客户端IP地址
     * 处理各种代理头，如X-Forwarded-For、X-Real-IP等
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        log.debug("开始获取客户端IP地址");
        
        String ipAddress = request.getHeader("X-Forwarded-For");
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // 处理X-Forwarded-For中的多个IP（逗号分隔）
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0];
        }
        
        log.debug("获取到客户端IP地址: {}", ipAddress);
        return ipAddress;
    }
    
    /**
     * 从当前请求上下文获取客户端IP地址
     * 此方法使用RequestContextHolder获取当前请求
     * @return 客户端IP地址
     */
    public static String getClientIpAddress() {
        log.debug("从当前请求上下文获取客户端IP地址");
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.debug("无法获取请求上下文");
            return null;
        }
        
        HttpServletRequest request = attributes.getRequest();
        return getClientIpAddress(request);
    }
}
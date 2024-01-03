package com.nuaa.art.common.utils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class ServletUtils {
    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return servletRequestAttributes.getRequest();
    }
     /*
      * 获取response
      */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return servletRequestAttributes.getResponse();
    }

    /**
     * 将请求参数转化为Map
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, Object> param = new HashMap<>();
        try {
            param.put("user", request.getHeader("user"));
            param.put("token", request.getHeader("token"));
            param.put("url", request.getRequestURL());
            Enumeration<String> em = request.getParameterNames();
            while (em.hasMoreElements()) {
                String key = em.nextElement();
                param.put(key, request.getParameter(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }
}

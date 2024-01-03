package com.nuaa.art.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

public class ThreadLocalUtils {
    private static final ThreadLocal<Map<String, Object>> requestHolder = new ThreadLocal<>();
    public static void setAttributes(Map<String, Object> attributes) {
        requestHolder.set(attributes);
    }
    public static Map<String, Object> getRequest() {
        return requestHolder.get();
    }
    public static void clear() {
        requestHolder.remove();
    }
}

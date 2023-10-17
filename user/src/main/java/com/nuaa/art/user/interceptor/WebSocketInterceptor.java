package com.nuaa.art.user.interceptor;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.user.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            // 模拟用户（通常利用JWT令牌解析用户信息）
//            String token = servletServerHttpRequest.getServletRequest().getParameter("token");
//            Claims claims= JwtUtils.verifyJwt(token);
//            if (claims == null) {
//                LogUtils.info("websocket-token无效，请求拦截");
//                return false;
//            } else {
//                attributes.put("user", (String) claims.get("username"));
//                return true;
//            }
            String user = servletServerHttpRequest.getServletRequest().getParameter("user");
            if(user == null  || user.isBlank()) return false;
            attributes.put("user", user);
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}

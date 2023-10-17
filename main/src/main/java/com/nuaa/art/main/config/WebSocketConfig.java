package com.nuaa.art.main.config;

import com.nuaa.art.common.websocket.WebSocketHandler;

import com.nuaa.art.user.interceptor.WebSocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    //WebSocket操作类的生成
    WebSocketHandler WebSocketHandler() {
        return new WebSocketHandler();
    }

    // 拦截器生成
    WebSocketInterceptor WebSocketInterceptor() {
        return new WebSocketInterceptor();
    }
    // 配置注册
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(WebSocketHandler(), "")
                .addInterceptors(WebSocketInterceptor())
                .setAllowedOrigins("*");
    }
}

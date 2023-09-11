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
    WebSocketHandler WebSocketHandler() {
        return new WebSocketHandler();
    }

    WebSocketInterceptor WebSocketInterceptor() {
        return new WebSocketInterceptor();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(WebSocketHandler(), "/console")
                .addInterceptors(WebSocketInterceptor())
                .setAllowedOrigins("*");
    }
}

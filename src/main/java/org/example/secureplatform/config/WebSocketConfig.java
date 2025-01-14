package org.example.secureplatform.config;

import org.example.secureplatform.controller.docker.DockerWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final DockerWebSocketHandler dockerWebSocketHandler;

    public WebSocketConfig(DockerWebSocketHandler dockerWebSocketHandler) {
        this.dockerWebSocketHandler = dockerWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(dockerWebSocketHandler, "/docker/container/exec")
                .setAllowedOrigins("*")  // 允许跨域请求（可以根据需求调整）
                .addInterceptors(new HttpSessionHandshakeInterceptor());  // 可选的拦截器
    }
}

package com.github.configuration;

import com.github.controller.websocket.HandshakeInterceptor;
import com.github.controller.websocket.SocketHandler;
import com.github.observer.WebSocketObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.Resource;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
@Configuration
@EnableWebSocket
public class ApplicationConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketHandlerDecoratorFactory webSocketHandlerDecoratorFactory;

    @Bean
    public ServerEndpointExporter serverEndpointExporter(ApplicationContext context) {
        return new ServerEndpointExporter();//开启@ServerEndpoint注解支持
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandlerDecoratorFactory.decorate(socketHandler()), "/five").
                addInterceptors(new HandshakeInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler socketHandler() {
        return new SocketHandler();
    }
}

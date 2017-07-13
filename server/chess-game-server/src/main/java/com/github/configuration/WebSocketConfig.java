package com.github.configuration;

import com.github.controller.websocket.HandshakeInterceptor;
import com.github.controller.websocket.handler.SocketHandler;
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
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketHandlerDecoratorFactory webSocketHandlerDecoratorFactory;

    @Resource(name = "baseWebSocketObserver")
    private WebSocketObserver baseWebSocketObserver;

    @Bean
    public ServerEndpointExporter serverEndpointExporter(ApplicationContext context) {
        return new ServerEndpointExporter();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandlerDecoratorFactory.decorate(socketHandler()), "/five").
                addInterceptors(new HandshakeInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler socketHandler() {
        SocketHandler socketHandler = new SocketHandler();
        socketHandler.addObserver(baseWebSocketObserver);
        return socketHandler;
    }
}

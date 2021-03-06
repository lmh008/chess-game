package com.github.controller.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 * 装扮者模式
 */
public class WebSocketHandlerDecorator implements WebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(WebSocketHandlerDecorator.class);

    private WebSocketHandler delegate;

    public WebSocketHandlerDecorator(WebSocketHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.debug(session.getId() + " connection established");
        delegate.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        logger.debug(session.getId() + " message receive");
        delegate.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error(session.getId() + " connect error!", exception);
        delegate.handleTransportError(session, exception);
        session.close();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.error(session.getId() + " connect closed!");
        delegate.afterConnectionClosed(session, closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return delegate.supportsPartialMessages();
    }
}

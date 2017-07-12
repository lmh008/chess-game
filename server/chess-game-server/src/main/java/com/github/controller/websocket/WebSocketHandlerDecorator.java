package com.github.controller.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
public class WebSocketHandlerDecorator implements WebSocketHandler {

    private WebSocketHandler delegate;

    public WebSocketHandlerDecorator(WebSocketHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        delegate.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        delegate.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        delegate.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        delegate.afterConnectionClosed(session, closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return delegate.supportsPartialMessages();
    }
}

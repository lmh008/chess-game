package com.github.controller.websocket.handler;

import com.alibaba.fastjson.JSON;
import com.github.entity.Message;
import com.github.observer.WebSocketObserver;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
public class SocketHandler extends TextWebSocketHandler {

    private List<WebSocketObserver> webSocketObservers;

    public SocketHandler() {
        this.webSocketObservers = new ArrayList<>();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        System.out.println(textMessage);
        Message message = JSON.parseObject(textMessage.getPayload(), Message.class);
        for (WebSocketObserver webSocketObserver : webSocketObservers) {
            webSocketObserver.respondMessage(session, message);
        }
        session.sendMessage(textMessage);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println(session.getId() + " connect!");
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println(session.getId() + " closed!");
        super.afterConnectionClosed(session, status);
    }

    public void addObserver(WebSocketObserver webSocketObserver) {
        this.webSocketObservers.add(webSocketObserver);
    }
}

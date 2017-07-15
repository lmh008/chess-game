package com.github.controller.websocket;

import com.alibaba.fastjson.JSONObject;
import com.github.controller.WebSocketRequestDispatch;
import com.github.observer.WebSocketObserver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
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
public class SocketHandler extends TextWebSocketHandler implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private List<WebSocketObserver> webSocketObservers;

    public SocketHandler() {
        this.webSocketObservers = new ArrayList<>();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        System.out.println(textMessage);
        JSONObject jsonData = JSONObject.parseObject(textMessage.getPayload());
        String topic = jsonData.getString("topic");
        String tag = jsonData.getString("tag");
        Assert.isTrue(StringUtils.hasText(topic) && StringUtils.hasText(tag), "un support request");
        applicationContext.getBean(WebSocketRequestDispatch.class).doDispatch(session, topic, tag, jsonData.get("data"));
        session.sendMessage(textMessage);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println(session.getId() + " connect!");
        for (WebSocketObserver webSocketObserver : webSocketObservers) {
            webSocketObserver.respondConnectionEstablished(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println(session.getId() + " closed!");
        for (WebSocketObserver webSocketObserver : webSocketObservers) {
            webSocketObserver.respondConnectionClosed(session);
        }
    }

    public void addObserver(WebSocketObserver webSocketObserver) {
        this.webSocketObservers.add(webSocketObserver);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
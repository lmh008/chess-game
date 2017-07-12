package com.github.service.observer;

import com.alibaba.fastjson.JSON;
import com.github.entity.Message;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
public abstract class MessageObserverTemplate implements WebSocketObserver {

    @Override
    public void respondMessage(WebSocketSession session, TextMessage textMessage) {
        Message message = JSON.parseObject(textMessage.getPayload(), Message.class);
        this.respondMessage(message);
    }

    @Override
    public void respondConnectionEstablished(WebSocketSession session) {

    }

    @Override
    public void respondConnectionClosed(WebSocketSession session) {

    }

    public abstract void respondMessage(Message message);
}

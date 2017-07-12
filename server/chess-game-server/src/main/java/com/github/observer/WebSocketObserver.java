package com.github.observer;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
public interface WebSocketObserver {

    void respondMessage(WebSocketSession session, TextMessage textMessage);

    void respondConnectionEstablished(WebSocketSession session);

    void respondConnectionClosed(WebSocketSession session);

}

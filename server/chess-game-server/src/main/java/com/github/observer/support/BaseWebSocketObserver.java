package com.github.observer.support;

import com.github.ApplicationContext;
import com.github.observer.WebSocketObserver;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
@Component("baseWebSocketObserver")
public class BaseWebSocketObserver implements WebSocketObserver {

    @Override
    public void respondConnectionEstablished(WebSocketSession session) {
        ApplicationContext.addOnlinePlayer(session);
    }

    @Override
    public void respondConnectionClosed(WebSocketSession session) {
        ApplicationContext.removePlayer(session);
    }

}

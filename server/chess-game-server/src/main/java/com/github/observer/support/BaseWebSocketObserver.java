package com.github.observer.support;

import com.github.ApplicationContext;
import com.github.entity.Player;
import com.github.observer.WebSocketObserver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
        ApplicationContext.allOnlinePlayer.put(session.getId(), new Player(session));
    }

    @Override
    public void respondConnectionClosed(WebSocketSession session) {
        ApplicationContext.allOnlinePlayer.remove(session.getId());
    }

}

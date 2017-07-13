package com.github.observer.support;

import com.github.command.Command;
import com.github.command.CommandFactory;
import com.github.entity.Constants;
import com.github.entity.Message;
import com.github.observer.WebSocketObserver;
import com.github.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BaseService baseService;

    @Override
    public void respondMessage(WebSocketSession session, Message message) throws Exception {
        if (Constants.BASE.equalsIgnoreCase(message.getTopic())) {
            Command<BaseService> command = CommandFactory.getCommand(message.getCommand());
            command.setReceiver(baseService);
            command.execute(session, message);
        }
    }

    @Override
    public void respondConnectionEstablished(WebSocketSession session) {

    }

    @Override
    public void respondConnectionClosed(WebSocketSession session) {

    }

}

package com.github.command;

import com.github.entity.Message;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/13.
 * Version v1.0
 */
public interface Command<T> {

    void execute(WebSocketSession session, Message message);

    void setReceiver(T receiver);

}

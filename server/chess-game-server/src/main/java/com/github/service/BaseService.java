package com.github.service;

import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
public interface BaseService {

    void setName(WebSocketSession webSocketSession, String message);

}

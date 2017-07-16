package com.github.controller;

import com.github.controller.dispatch.WebSocketMapping;
import com.github.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/13.
 * Version v1.0
 */
@Component
@WebSocketMapping("base")
public class BaseHandler {

    @Autowired
    private BaseService baseService;

    @WebSocketMapping("setName")
    public void setName(WebSocketSession session, String name) {
        baseService.setName(session, name);
    }

}

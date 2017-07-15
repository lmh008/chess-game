package com.github.controller.handler;

import com.github.controller.WebSocketMapping;
import com.github.service.BaseService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/13.
 * Version v1.0
 */
@Component("baseHandler")
@WebSocketMapping("base")
public class BaseHandler {

    private BaseService baseService;

    @WebSocketMapping("setName")
    public void setName(WebSocketSession session, String name) {
        baseService.setName(session, name);
    }

}

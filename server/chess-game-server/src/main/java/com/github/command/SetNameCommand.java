package com.github.command;

import com.github.entity.Message;
import com.github.service.BaseService;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/13.
 * Version v1.0
 */
public class SetNameCommand implements Command<BaseService> {

    private BaseService baseService;

    @Override
    public void execute(WebSocketSession session, Message message) {
        baseService.setName(session, message);
    }

    @Override
    public void setReceiver(BaseService receiver) {
        this.baseService = (BaseService) receiver;
    }

}

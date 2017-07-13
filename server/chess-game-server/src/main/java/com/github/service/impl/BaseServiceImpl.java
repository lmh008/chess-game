package com.github.service.impl;

import com.github.controller.ApplicationContext;
import com.github.entity.Message;
import com.github.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
@Service("baseService")
public class BaseServiceImpl implements BaseService {

    @Override
    public void setName(WebSocketSession webSocketSession, Message message) {
        ApplicationContext.addEffectivePlayer((String) message.getData(), webSocketSession);
    }
}

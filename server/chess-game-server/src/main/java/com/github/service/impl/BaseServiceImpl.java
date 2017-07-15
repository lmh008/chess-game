package com.github.service.impl;

import com.github.ApplicationContext;
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
    public void setName(WebSocketSession webSocketSession, String name) {
        ApplicationContext.addEffectivePlayer(name, webSocketSession);
    }
}

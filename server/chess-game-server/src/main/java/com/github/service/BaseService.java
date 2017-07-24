package com.github.service;

import com.github.ApplicationContext;
import com.github.controller.dispatch.WebSocketMapping;
import com.github.entity.Player;
import org.springframework.stereotype.Service;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
@Service("baseService")
@WebSocketMapping("base")
public class BaseService {

    @WebSocketMapping("setName")
    public void setName(Player player, String name) {
        player.setName(name);
    }

    @WebSocketMapping("startQueue")
    public void startQueue(Player player) {
        ApplicationContext.waitQueue.add(player);
    }
}

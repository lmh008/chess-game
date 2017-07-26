package com.github.service;

import com.github.ApplicationContext;
import com.github.controller.dispatch.WebSocketMapping;
import com.github.entity.Player;
import com.github.observer.WebSocketObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
@Service("baseService")
@WebSocketMapping("base")
public class BaseService implements WebSocketObserver {

    private final Logger logger = LoggerFactory.getLogger(BaseService.class);

    //创建线程安全的List，这里其实就是用SynchronizedListd的代理模式
    private static List<Player> waitQueue = Collections.synchronizedList(new LinkedList<Player>());

    @Autowired
    private GameService gameService;

    @WebSocketMapping("setName")
    public void setName(Player player, String name) {
        player.setName(name);
    }

    @WebSocketMapping("startQueue")
    public void startQueue(Player player) {
        waitQueue.add(player);
    }


    @Override
    public void respondConnectionEstablished(WebSocketSession session) {
        ApplicationContext.allOnlinePlayer.put(session.getId(), new Player(session));
    }

    @Override
    public void respondConnectionClosed(WebSocketSession session) {
        ApplicationContext.allOnlinePlayer.remove(session.getId());
        for (int i = 0; i < waitQueue.size(); i++) {
            if (waitQueue.get(i).getId().equals(session.getId())) {
                waitQueue.remove(i);
                return;
            }
        }
    }

    public void matchPlayer() {
        if (waitQueue.size() >= 2) {
            Player player1 = waitQueue.remove(0);
            Player player2 = waitQueue.remove(0);
            gameService.prepareGame(player1, player2);
            logger.info("player matching... current wait queue size : " + waitQueue.size());
        }
    }

    public void sendPlayersInfo() {
        HashMap<String, Integer> data = new HashMap<>();
        data.put("online", ApplicationContext.allOnlinePlayer.size());
        data.put("onWait", waitQueue.size());
        for (Map.Entry<String, Player> entry : ApplicationContext.allOnlinePlayer.entrySet()) {
            if (StringUtils.hasText(entry.getValue().getName())) {
                try {
                    entry.getValue().sendMessage("base", "playerInfos", data);
                } catch (IOException e) {
                    logger.error("send Message error! ", e);
                }
            }
        }
    }
}

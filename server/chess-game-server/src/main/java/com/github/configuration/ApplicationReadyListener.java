package com.github.configuration;

import com.github.ApplicationContext;
import com.github.controller.dispatch.WebSocketRequestDispatch;
import com.github.entity.Player;
import com.github.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/15.
 * Version v1.0
 */
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    private final Logger logger = LoggerFactory.getLogger(ApplicationReadyListener.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        BeanFactory beanFactory = event.getApplicationContext().getBeanFactory();
        WebSocketRequestDispatch webSocketRequestDispatch = beanFactory.getBean(WebSocketRequestDispatch.class);
        webSocketRequestDispatch.init();
        GameService gameService = beanFactory.getBean(GameService.class);
        new Thread(() -> {
            logger.info("player matching... ");
            //noinspection InfiniteLoopStatement
            while (true) {
                if (ApplicationContext.waitQueue.size() >= 2) {
                    Player player1 = ApplicationContext.waitQueue.remove(0);
                    Player player2 = ApplicationContext.waitQueue.remove(0);
                    gameService.prepareGame(player1, player2);
                    logger.info("player matching... current wait queue size : " + ApplicationContext.waitQueue.size());
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            logger.info("send msg playerInfos : ----------------------");
            //noinspection InfiniteLoopStatement
            while (true) {
                HashMap<String, Integer> data = new HashMap<>();
                data.put("online", ApplicationContext.allOnlinePlayer.size());
                data.put("onWait", ApplicationContext.waitQueue.size());
                for (Map.Entry<String, Player> entry : ApplicationContext.allOnlinePlayer.entrySet()) {
                    if (StringUtils.hasText(entry.getValue().getName())) {
                        try {
                            entry.getValue().sendMessage("base", "playerInfos", data);
                        } catch (IOException e) {
                            logger.error("send Message error! ", e);
                        }
                    }
                }
                logger.info("send msg playerInfos : " + ApplicationContext.allOnlinePlayer.size());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

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
import org.springframework.core.task.TaskExecutor;

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
        TaskExecutor taskExecutor = beanFactory.getBean(TaskExecutor.class);
        GameService gameService = beanFactory.getBean(GameService.class);
        taskExecutor.execute(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                if (ApplicationContext.waitQueue.size() > 2) {
                    Player player1 = ApplicationContext.waitQueue.remove(0);
                    Player player2 = ApplicationContext.waitQueue.remove(0);
                    gameService.prepareGame(player1, player2);
                    logger.info("game start: 【" + player1.getName() + "】" +
                            "【" + player2.getName() + "】");
                }
                logger.info("player matching... current wait queue size : " + ApplicationContext.waitQueue.size());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

package com.github.configuration;

import com.github.controller.dispatch.WebSocketRequestDispatch;
import com.github.controller.websocket.SocketHandler;
import com.github.observer.WebSocketObserver;
import com.github.service.BaseService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/**
 * spring 容器监听器
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/15.
 * Version v1.0
 */
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();

        WebSocketRequestDispatch webSocketRequestDispatch = applicationContext.getBean(WebSocketRequestDispatch.class);
        webSocketRequestDispatch.init();

        Map<String, WebSocketObserver> observerMap = applicationContext.getBeansOfType(WebSocketObserver.class);
        if (observerMap != null && observerMap.size() > 0) {
            SocketHandler socketHandler = applicationContext.getBean(SocketHandler.class);
            for (WebSocketObserver webSocketObserver : observerMap.values()) {
                socketHandler.addObserver(webSocketObserver);
            }
        }

        BaseService baseService = applicationContext.getBean(BaseService.class);
        new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                baseService.matchPlayer();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                baseService.sendPlayersInfo();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

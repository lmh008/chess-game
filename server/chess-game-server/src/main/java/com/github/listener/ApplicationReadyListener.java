package com.github.listener;

import com.github.controller.WebSocketRequestDispatch;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import javax.servlet.annotation.WebListener;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/15.
 * Version v1.0
 */
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        WebSocketRequestDispatch webSocketRequestDispatch = event.getApplicationContext().getBeanFactory().getBean(WebSocketRequestDispatch.class);
        webSocketRequestDispatch.initHandlerMapping();
    }

}

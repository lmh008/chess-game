package com.github.service.observer.impl;

import com.github.entity.Constants;
import com.github.service.observer.MessageObserverTemplate;
import com.github.entity.Message;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
public class BaseWebSocketObserver extends MessageObserverTemplate {

    @Override
    public void respondMessage(Message message) {
        if (Constants.BASE.equals(message.getTopic())) {
            
        }
    }

}

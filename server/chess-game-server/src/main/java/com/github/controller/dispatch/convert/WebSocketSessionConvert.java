package com.github.controller.dispatch.convert;

import com.github.ApplicationContext;
import com.github.entity.Player;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/16.
 * Version v1.0
 */
public class WebSocketSessionConvert implements ParamsConvert {

    @Override
    public Object convertParams(DataInfo dataInfo, String parameterName, Class<?> parameterType, ParamsConvertChain convertChain) {
        if (parameterType == WebSocketSession.class) {
            return dataInfo.getWebSocketSession();
        } else if (parameterType == Player.class) {
            return dataInfo.getPlayer();
        } else {
            return convertChain.convertParams(dataInfo, parameterName,
                    parameterType, convertChain);
        }
    }
}

package com.github.controller.dispatch.convert;

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
        return parameterType == WebSocketSession.class ? dataInfo.getWebSocketSession() : convertChain.convertParams(dataInfo, parameterName,
                parameterType, convertChain);
    }
}

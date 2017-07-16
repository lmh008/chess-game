package com.github.controller.dispatch.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/16.
 * Version v1.0
 */
public class DataInfo {

    private JSONArray dataToJsonArray;
    private JSONObject dataToJsonObject;
    private String dataToJsonStr;
    private Object data;
    private WebSocketSession webSocketSession;

    public DataInfo(Object data, WebSocketSession session) {
        this.webSocketSession = session;
        this.data = data;
        dataToJsonArray = null;
        dataToJsonObject = null;
        dataToJsonStr = null;
        if (data instanceof JSONArray) {
            dataToJsonArray = (JSONArray) data;
        }
        if (data instanceof JSONObject) {
            dataToJsonObject = (JSONObject) data;
            dataToJsonStr = JSON.toJSONString(data);
        }
    }

    public JSONArray getDataToJsonArray() {
        return dataToJsonArray;
    }

    public JSONObject getDataToJsonObject() {
        return dataToJsonObject;
    }

    public String getDataToJsonStr() {
        return dataToJsonStr;
    }

    public Object getData() {
        return data;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }
}

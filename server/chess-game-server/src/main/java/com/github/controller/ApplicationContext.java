package com.github.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
@Component
public class ApplicationContext {

    private static Map<String, WebSocketSession> allOnlinePlayer = new ConcurrentHashMap<>();

    private static Map<String, WebSocketSession> effectivePlayer = new ConcurrentHashMap<>();

    public static void dealMessage(TextMessage textMessage) {
        JSONObject jsonObject = (JSONObject) JSON.parse(textMessage.getPayload());
        String msgType = jsonObject.getString("msgType");
    }

    public static void addOnlinePlayer(WebSocketSession session) {
        allOnlinePlayer.put(session.getId(), session);
    }

    public static void addEffectivePlayer(String name, WebSocketSession session) {
        effectivePlayer.put(name, session);
        allOnlinePlayer.get(session.getId()).getAttributes().put("name", name);
    }

    public static void removePlayer(WebSocketSession session) {
        String name = (String) session.getAttributes().get("name");
        allOnlinePlayer.remove(session.getId());
        if (StringUtils.hasText(name)) {
            effectivePlayer.remove(name);
        }
    }

    public static void removePlayer(String name) {
        String sessionId = effectivePlayer.get(name).getId();
        effectivePlayer.remove(name);
        allOnlinePlayer.remove(sessionId);
    }

    public static WebSocketSession getSession(WebSocketSession session) {
        return allOnlinePlayer.get(session.getId());
    }

    public static WebSocketSession getSession(String name) {
        return effectivePlayer.get(name);
    }
}

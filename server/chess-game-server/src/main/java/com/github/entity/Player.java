package com.github.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/24.
 * Version v1.0
 */
public class Player {

    @JSONField(serialize = false)
    private WebSocketSession webSocketSession;

    @JSONField(serialize = false)
    private String id;

    private String name;

    private int color;

    private int winCount;

    private int lossCount;

    @JSONField(serialize = false)
    private boolean isReady;

    @JSONField(serialize = false)
    private boolean isGaming;

    private Player opponent;

    public Player(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
        this.id = webSocketSession.getId();
        this.name = (String) webSocketSession.getAttributes().get("name");
    }

    public void addWinCount() {
        this.winCount++;
    }

    public void addLossCount() {
        this.lossCount++;
    }

    public void changeColor() {
        this.color = -this.color;
    }

    public void sendMessage(String topic, String tag) throws IOException {
        this.sendMessage(new Message(topic, tag));
    }

    public void sendMessage(String topic, String tag, Object data) throws IOException {
        this.sendMessage(new Message(topic, tag, data));
    }

    public void sendMessage(Message message) throws IOException {
        this.sendMessage(message.toTextMessage());
    }

    public void sendMessage(TextMessage textMessage) throws IOException {
        synchronized (webSocketSession) {
            webSocketSession.sendMessage(textMessage);
        }
    }

    public void reset() {
        this.color = Constants.COLOR_NULL;
        this.opponent = null;
        this.isReady = false;
        this.lossCount = 0;
        this.winCount = 0;
        this.isGaming = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getLossCount() {
        return lossCount;
    }

    public void setLossCount(int lossCount) {
        this.lossCount = lossCount;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public boolean isGaming() {
        return isGaming;
    }

    public void setGaming(boolean gaming) {
        isGaming = gaming;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", winCount=" + winCount +
                ", lossCount=" + lossCount +
                ", isReady=" + isReady +
                ", isGaming=" + isGaming +
                ", opponent=" + opponent +
                '}';
    }
}

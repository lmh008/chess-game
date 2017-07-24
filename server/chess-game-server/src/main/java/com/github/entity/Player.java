package com.github.entity;

import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/24.
 * Version v1.0
 */
public class Player {

    private WebSocketSession webSocketSession;

    private String sessionId;

    private String name;

    private int color;

    private int winCount;

    private int lossCount;

    private boolean isReady;

    private boolean isGaming;

    private Player opponent;

    public Player(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
        this.sessionId = webSocketSession.getId();
        this.name = (String) webSocketSession.getAttributes().get("name");
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public String getSessionId() {
        return sessionId;
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
                "sessionId='" + sessionId + '\'' +
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

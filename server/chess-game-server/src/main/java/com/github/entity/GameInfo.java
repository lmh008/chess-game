package com.github.entity;

import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/24.
 * Version v1.0
 */
public class GameInfo {

    private int[][] chesses = new int[25][25];

    private Player player1;

    private Player player2;

    private String currentUnderPawnId;

    public GameInfo(Player player1, Player player2) {
        Assert.isTrue(player1 != null && player2 != null, "can not null!");
        this.player1 = player1;
        this.player2 = player2;
    }

    public int[][] getChesses() {
        return chesses;
    }

    public void renew() {
        chesses = new int[25][25];
        player1.setColor(-player1.getColor());
        player2.setColor(-player2.getColor());
        player1.setReady(false);
        player2.setReady(false);
    }

    public String getCurrentUnderPawnId() {
        return currentUnderPawnId;
    }

    public void setCurrentUnderPawnId(String currentUnderPawnId) {
        this.currentUnderPawnId = currentUnderPawnId;
    }

    public Player getPlayer(WebSocketSession session) {
        return this.getPlayer(session.getId());
    }

    private Player getPlayer(String id) {
        if (player1.getSessionId().equals(id)) {
            return player1;
        } else if (player2.getSessionId().equals(id)) {
            return player2;
        } else {
            return null;
        }
    }
}

package com.github.service.impl;

import com.github.entity.Constants;
import com.github.entity.GameInfo;
import com.github.entity.Message;
import com.github.entity.Player;
import com.github.observer.WebSocketObserver;
import com.github.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/24.
 * Version v1.0
 */
@Service("gameService")
public class GameServiceImpl implements GameService, WebSocketObserver {

    private final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    private static Map<String, GameInfo> gameInfoMap = new ConcurrentHashMap<>();

    private final String topic = "game";

    @Override
    public void prepareGame(Player player1, Player player2) {
        player1.setOpponent(player2);
        player2.setOpponent(player1);
        int color = Math.random() > 0.5 ? Constants.COLOR_BLACK : Constants.COLOR_WHITE;
        player1.setColor(color);
        player2.setColor(-color);
        player2.setGaming(true);
        player1.setGaming(true);
        GameInfo gameInfo = new GameInfo(player1, player2);
        gameInfoMap.put(player1.getSessionId(), gameInfo);
        gameInfoMap.put(player2.getSessionId(), gameInfo);
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("opponent", player1.getOpponent().getName());
            Message message = new Message(topic, "prepare", data);
            player1.getWebSocketSession().sendMessage(message.toTextMessage());
            //noinspection unchecked
            ((Map) message.getData()).put("opponent", player2.getOpponent().getName());
            player2.getWebSocketSession().sendMessage(message.toTextMessage());
        } catch (IOException e) {
            this.afterException(player1, player2, e);
        }
    }

    @Override
    public void playerReady(Player player) {
        player.setReady(true);
        Player opponent = player.getOpponent();
        if (opponent.isReady()) {
            GameInfo gameInfo = gameInfoMap.get(player.getSessionId());
            TextMessage textMessage = new Message(topic, "start").toTextMessage();
            gameInfo.setCurrentUnderPawnId(player.getColor() == Constants.COLOR_BLACK ? player.getSessionId() : opponent.getSessionId());
            try {
                player.getWebSocketSession().sendMessage(textMessage);
                opponent.getWebSocketSession().sendMessage(textMessage);
            } catch (IOException e) {
                this.afterException(player, e);
            }
        }
    }

    @Override
    public void underPawn(Player player, Point point) {
        GameInfo gameInfo = gameInfoMap.get(player.getSessionId());
        gameInfo.setCurrentUnderPawnId(player.getOpponent().getSessionId());
        int[][] chesses = gameInfo.getChesses();
        Assert.isTrue(point.x <= chesses.length || point.y <= chesses.length, "wrong point!");
        chesses[point.x][point.y] = player.getColor();
        boolean winFlag = this.isWin(chesses, point);
    }

    private boolean isWin(int[][] chesses, Point p) {
        int lastColor = chesses[p.x][p.y];
        int num = 0;
        int tempIndex = 0;
        Point minPoint = new Point(tempIndex = p.x - 4 > 0 ? tempIndex : 0, tempIndex = p.y - 4 > 0 ? tempIndex : 0);
        Point maxPoint = new Point(tempIndex = p.x + 4 > chesses.length ? chesses.length : tempIndex,
                tempIndex = p.y + 4 > chesses.length ? chesses.length : tempIndex);
        for (int x = minPoint.x; x <= maxPoint.x; x++) {
            if (chesses[x][p.y] == lastColor) {
                num++;
                if (num == 5) {
                    return true;
                }
            } else {
                num = 0;
            }
        }
        for (int y = minPoint.y; y <= maxPoint.y; y++) {
            if (chesses[p.x][y] == lastColor) {
                num++;
                if (num == 5) {
                    return true;
                }
            } else {
                num = 0;
            }
        }
        return false;
    }

    @Override
    public void chat(Player player, String message) {

    }

    @Override
    public void requestRegret(Player player) {

    }

    @Override
    public void responseRegret(Player player) {

    }

    @Override
    public void giveUp(Player player) {

    }

    @Override
    public void respondConnectionClosed(WebSocketSession session) {
        GameInfo gameInfo;
        if ((gameInfo = gameInfoMap.get(session.getId())) != null) {
            Player lostPlayer = gameInfo.getPlayer(session);
            Player opponent = lostPlayer.getOpponent();
            this.resetPlayer(opponent);
            try {
                opponent.getWebSocketSession().sendMessage(new Message(topic, "lostOpponent", null).toTextMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            gameInfoMap.remove(lostPlayer.getSessionId());
            gameInfoMap.remove(opponent.getSessionId());
        }
    }

    private void resetPlayer(Player player) {
        player.setColor(Constants.COLOR_NULL);
        player.setOpponent(null);
        player.setReady(false);
        player.setLossCount(0);
        player.setWinCount(0);
        player.setGaming(false);
    }

    private void afterException(Player player, Exception e) {
        this.afterException(player, player.getOpponent(), e);
    }

    private void afterException(Player player1, Player player2, Exception e) {
        TextMessage textMessage = new Message(topic, "error").toTextMessage();
        try {
            player1.getWebSocketSession().sendMessage(textMessage);
            player2.getWebSocketSession().sendMessage(textMessage);
        } catch (IOException e1) {
            logger.error("", e1);
        } finally {
            this.resetPlayer(player1);
            this.resetPlayer(player2);
            gameInfoMap.remove(player1.getSessionId());
            gameInfoMap.remove(player2.getSessionId());
            logger.error("game service has error!", e);
        }
    }

    @Override
    public void respondConnectionEstablished(WebSocketSession session) {
    }

}

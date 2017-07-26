package com.github.service;

import com.github.controller.dispatch.WebSocketMapping;
import com.github.entity.Constants;
import com.github.entity.GameInfo;
import com.github.entity.Message;
import com.github.entity.Player;
import com.github.observer.WebSocketObserver;
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
@WebSocketMapping("game")
public class GameService implements WebSocketObserver {

    private final Logger logger = LoggerFactory.getLogger(GameService.class);

    private static Map<String, GameInfo> gameInfoMap = new ConcurrentHashMap<>();

    private final String topic = "game";

    @WebSocketMapping("prepareGame")
    public void prepareGame(Player player1, Player player2) {
        player1.setOpponent(player2);
        player2.setOpponent(player1);
        int color = Math.random() > 0.5 ? Constants.COLOR_BLACK : Constants.COLOR_WHITE;
        player1.setColor(color);
        player2.setColor(-color);
        player2.setGaming(true);
        player1.setGaming(true);
        GameInfo gameInfo = new GameInfo(player1, player2);
        gameInfoMap.put(player1.getId(), gameInfo);
        gameInfoMap.put(player2.getId(), gameInfo);
        try {
            Message message = new Message(topic, "prepareGame", player1.getOpponent().getName());
            player1.sendMessage(message.toTextMessage());
            //noinspection unchecked
            message.setData(player2.getOpponent().getName());
            player2.sendMessage(message.toTextMessage());
        } catch (IOException e) {
            this.afterException(player1, player2, e);
        }
    }

    @WebSocketMapping("playerReady")
    public void playerReady(Player player) {
        player.setReady(true);
        Player opponent = player.getOpponent();
        if (opponent.isReady()) {
            GameInfo gameInfo = gameInfoMap.get(player.getId());
            Message message = new Message(topic, "start");
            gameInfo.setCurrentUnderPawnId(player.getColor() == Constants.COLOR_BLACK ? player.getId() : opponent.getId());
            try {
                message.setData(player.getColor());
                player.sendMessage(message);
                message.setData(opponent.getColor());
                opponent.sendMessage(message);
            } catch (IOException e) {
                this.afterException(player, e);
            }
        }
    }

    @WebSocketMapping("underPawn")
    public void underPawn(Player player, Point point) {
        GameInfo gameInfo = gameInfoMap.get(player.getId());
        Assert.isTrue(player.getId().equals(gameInfo.getCurrentUnderPawnId()), "wrong states!");
        gameInfo.backUp();
        boolean winFlag = gameInfo.underPawn(point, player.getColor());
        Player opponent = player.getOpponent();
        try {
            opponent.sendMessage(topic, "underPawn", point);
            if (winFlag) {
                player.addWinCount();
                opponent.addLossCount();
                gameInfo.renew();
                player.sendMessage(topic, "win");
                opponent.sendMessage(topic, "loss");
            } else {
                gameInfo.setCurrentUnderPawnId(opponent.getId());
            }
        } catch (IOException e) {
            this.afterException(player, opponent, e);
        }
    }

    @WebSocketMapping("chat")
    public void chat(Player player, String message) {
        Player opponent = player.getOpponent();
        try {
            opponent.sendMessage(topic, "chat", message);
        } catch (IOException e) {
            this.afterException(player, opponent, e);
        }
    }

    @WebSocketMapping("requestRegret")
    public void requestRegret(Player player) {
        GameInfo gameInfo = gameInfoMap.get(player.getId());
        Assert.isTrue(!gameInfo.getCurrentUnderPawnId().equals(player.getId()), "wrong state!");
        Player opponent = player.getOpponent();
        try {
            opponent.sendMessage(topic, "requestRegret");
        } catch (IOException e) {
            this.afterException(player, opponent, e);
        }
    }

    @WebSocketMapping("responseRegret")
    public void responseRegret(Player player, boolean agree) {
        GameInfo gameInfo = gameInfoMap.get(player.getId());
        Player opponent = player.getOpponent();
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("result", agree);
            if (agree) {
                GameInfo.BoardState boardState = gameInfo.recover();
                data.put("boardState", boardState);
                player.sendMessage(topic, "regretSynchronized", boardState);
            }
            opponent.sendMessage(topic, "responseRegret", data);
        } catch (IOException e) {
            this.afterException(player, opponent, e);
        }
    }

    @WebSocketMapping("giveUp")
    public void giveUp(Player player) {
        GameInfo gameInfo = gameInfoMap.get(player.getId());
        Player opponent = player.getOpponent();
        opponent.addWinCount();
        player.addLossCount();
        gameInfo.renew();
        try {
            opponent.sendMessage(topic, "giveUp");
        } catch (IOException e) {
            this.afterException(player, opponent, e);
        }
    }

    private void afterException(Player player, Exception e) {
        this.afterException(player, player.getOpponent(), e);
    }

    private void afterException(Player player1, Player player2, Exception e) {
        TextMessage textMessage = new Message(topic, "error").toTextMessage();
        try {
            player1.sendMessage(textMessage);
            player2.sendMessage(textMessage);
        } catch (IOException e1) {
            logger.error("", e1);
        } finally {
            player1.reset();
            player2.reset();
            gameInfoMap.remove(player1.getId());
            gameInfoMap.remove(player2.getId());
            logger.error("game service has error!", e);
        }
    }

    @Override
    public void respondConnectionClosed(WebSocketSession session) {
        GameInfo gameInfo;
        if ((gameInfo = gameInfoMap.get(session.getId())) != null) {
            Player lostPlayer = gameInfo.getPlayer(session);
            Player opponent = lostPlayer.getOpponent();
            opponent.reset();
            try {
                opponent.sendMessage(new Message(topic, "lostOpponent", null).toTextMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            gameInfoMap.remove(lostPlayer.getId());
            gameInfoMap.remove(opponent.getId());
        }
    }

    @Override
    public void respondConnectionEstablished(WebSocketSession session) {
    }

}

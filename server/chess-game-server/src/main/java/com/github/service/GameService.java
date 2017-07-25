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
            Map<String, Object> data = new HashMap<>();
            data.put("opponent", player1.getOpponent().getName());
            data.put("color", player1.getColor());
            Message message = new Message(topic, "prepareGame", data);
            player1.sendMessage(message.toTextMessage());
            //noinspection unchecked
            data.put("opponent", player2.getOpponent().getName());
            data.put("color", player2.getColor());
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
            TextMessage textMessage = new Message(topic, "start").toTextMessage();
            gameInfo.setCurrentUnderPawnId(player.getColor() == Constants.COLOR_BLACK ? player.getId() : opponent.getId());
            try {
                player.sendMessage(textMessage);
                opponent.sendMessage(textMessage);
            } catch (IOException e) {
                this.afterException(player, e);
            }
        }
    }

    @WebSocketMapping("underPawn")
    public void underPawn(Player player, Point point) {
        GameInfo gameInfo = gameInfoMap.get(player.getId());
        Assert.isTrue(player.getId().equals(gameInfo.getCurrentUnderPawnId()), "wrong states!");
        gameInfo.setCurrentUnderPawnId(player.getOpponent().getId());
        int[][] chesses = gameInfo.getChesses();
        Assert.isTrue(point.x <= chesses.length || point.y <= chesses.length, "wrong point!");
        chesses[point.x][point.y] = player.getColor();
        boolean winFlag = this.isWin(chesses, point);
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

    }

    @WebSocketMapping("requestRegret")
    public void requestRegret(Player player) {

    }

    @WebSocketMapping("responseRegret")
    public void responseRegret(Player player) {

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

    private boolean isWin(int[][] chesses, Point p) {
        int lastColor = chesses[p.x][p.y];
        int length = chesses.length;
        int num = 0;
        int temp = 0;
        Point minPoint = new Point((temp = p.x - 4) > 0 ? temp : 0, (temp = p.y - 4) > 0 ? temp : 0);
        Point maxPoint = new Point((temp = p.x + 4) > length ? length : temp,
                (temp = p.y + 4) > length ? length : temp);
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
        num = 0;
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
        minPoint = new Point(p.x - 4, p.y - 4);
        if (minPoint.x < 0 || minPoint.y < 0) {
            temp = minPoint.x < minPoint.y ? 0 - minPoint.x : 0 - maxPoint.y;
            minPoint.x = minPoint.x + temp;
            minPoint.y = minPoint.y + temp;
        }
        maxPoint = new Point(p.x + 4, p.y + 4);
        if (maxPoint.x > length || maxPoint.y > length) {
            temp = maxPoint.x > maxPoint.y ? maxPoint.x - length : maxPoint.y - length;
            minPoint.x = minPoint.x - temp;
            minPoint.y = minPoint.y - temp;
        }
        num = 0;
        for (int x = minPoint.x, y = minPoint.y; x <= maxPoint.x; x = ++y) {
            if (chesses[x][y] == lastColor) {
                num++;
                if (num == 5) {
                    return true;
                }
            } else {
                num = 0;
            }
        }
        num = 0;
        for (int x = minPoint.x, y = maxPoint.y; x <= maxPoint.x; x++, y--) {
            if (chesses[x][y] == lastColor) {
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
    public void respondConnectionEstablished(WebSocketSession session) {
    }

}

package com.github.entity;

import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;


/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/24.
 * Version v1.0
 */
public class GameInfo {

    private BoardState boardState = new BoardState();

    private LinkedList<BoardState> boardStateBacks = new LinkedList<>();

    private Player player1;

    private Player player2;

    public GameInfo(Player player1, Player player2) {
        Assert.isTrue(player1 != null && player2 != null, "can not null!");
        this.player1 = player1;
        this.player2 = player2;
        this.boardState.currentUnderPawnId = player1.getColor() == Constants.COLOR_BLACK ? player1.getId() : player2.getId();
    }

    public int[][] getChesses() {
        return this.boardState.chesses;
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public void renew() {
        player1.changeColor();
        player2.changeColor();
        player1.setReady(false);
        player2.setReady(false);
        boardState = new BoardState();
        this.boardState.currentUnderPawnId = player1.getColor() == Constants.COLOR_BLACK ? player1.getId() : player2.getId();
        boardStateBacks.clear();
    }

    public boolean underPawn(Point point, int color) {
        Assert.isTrue(point.x <= this.boardState.chesses.length || point.y <= this.boardState.chesses.length, "wrong point!");
        this.boardState.chesses[point.x][point.y] = color;
        this.boardState.lastPoint = point;
        return this.isWin(this.boardState.chesses, point);
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

    public String getCurrentUnderPawnId() {
        return this.boardState.currentUnderPawnId;
    }

    public void setCurrentUnderPawnId(String currentUnderPawnId) {
        this.boardState.currentUnderPawnId = currentUnderPawnId;
    }

    public Player getPlayer(WebSocketSession session) {
        return this.getPlayer(session.getId());
    }

    private Player getPlayer(String id) {
        if (player1.getId().equals(id)) {
            return player1;
        } else if (player2.getId().equals(id)) {
            return player2;
        } else {
            return null;
        }
    }

    public BoardState recover() {
        if (boardStateBacks.size() > 0) {
            this.boardState = boardStateBacks.size() == 1 ? boardStateBacks.get(0) : boardStateBacks.removeLast();
        }
        return this.boardState;
    }

    public void backUp() {
        this.boardStateBacks.add(this.boardState.backUp());
    }

    public class BoardState {

        private int[][] chesses = new int[25][25];
        private Point lastPoint;
        private String currentUnderPawnId;

        BoardState() {
        }

        BoardState(int[][] chesses, Point lastPoint, String currentUnderPawnId) {
            this.chesses = chesses;
            this.lastPoint = lastPoint;
            this.currentUnderPawnId = currentUnderPawnId;
        }

        BoardState backUp() {
            int[][] backChesses = new int[25][25];
            for (int i = 0; i < this.chesses.length; i++) {
                System.arraycopy(this.chesses[i], 0, backChesses[i], 0, this.chesses[i].length);
            }
            return new BoardState(backChesses, this.lastPoint, this.currentUnderPawnId);
        }

        public int[][] getChesses() {
            return chesses;
        }

        public void setChesses(int[][] chesses) {
            this.chesses = chesses;
        }

        public Point getLastPoint() {
            return lastPoint;
        }

        public void setLastPoint(Point lastPoint) {
            this.lastPoint = lastPoint;
        }

        public String getCurrentUnderPawnId() {
            return currentUnderPawnId;
        }

        public void setCurrentUnderPawnId(String currentUnderPawnId) {
            this.currentUnderPawnId = currentUnderPawnId;
        }
    }

}

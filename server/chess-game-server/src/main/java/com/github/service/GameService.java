package com.github.service;

import com.github.entity.Player;

import java.awt.*;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/24.
 * Version v1.0
 */
public interface GameService {

    void prepareGame(Player player1, Player player2);

    void playerReady(Player player);

    void underPawn(Player player, Point point);

    void chat(Player player, String message);

    void requestRegret(Player player);

    void responseRegret(Player player);

    void giveUp(Player player);
}

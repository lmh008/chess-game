package com.github;

import com.github.entity.Player;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.LinkedList;
import java.util.List;
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

    public static Map<String, Player> allOnlinePlayer = new ConcurrentHashMap<>();

    public static List<Player> waitQueue = new LinkedList<>();
}

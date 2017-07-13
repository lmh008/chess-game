package com.github.command;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/13.
 * Version v1.0
 */
public class CommandFactory {

    public static <T> Command<T> getCommand(String name) throws Exception {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        Class<Command<T>> clazz = (Class<Command<T>>) Class.forName("com.github.command." + name + "Command");
        return clazz.newInstance();
    }
}

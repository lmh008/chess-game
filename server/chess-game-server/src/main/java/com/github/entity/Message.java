package com.github.entity;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */
public class Message {

    private String topic;

    private String command;

    private Object data;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

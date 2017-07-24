package com.github.entity;

import com.alibaba.fastjson.JSON;
import org.springframework.web.socket.TextMessage;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/24.
 * Version v1.0
 */
public class Message {

    private String topic;

    private String tag;

    private Object data;

    public Message(String topic, String tag) {
        this.topic = topic;
        this.tag = tag;
    }

    public Message(String topic, String tag, Object data) {
        this.topic = topic;
        this.tag = tag;
        this.data = data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public TextMessage toTextMessage() {
        return new TextMessage(JSON.toJSONString(this));
    }
}

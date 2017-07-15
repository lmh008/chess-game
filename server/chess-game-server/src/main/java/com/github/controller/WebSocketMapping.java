package com.github.controller;

import java.lang.annotation.*;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/15.
 * Version v1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebSocketMapping {

    String value();

}

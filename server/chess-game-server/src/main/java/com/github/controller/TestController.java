package com.github.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/15.
 * Version v1.0
 */
@RestController("test")
public class TestController {

    @RequestMapping("/hello")
    public String hello(String name, int age) {
        System.out.println(name + "  " + age);
        return "hello";
    }
}

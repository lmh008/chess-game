package com.github;

import com.github.listener.ApplicationReadyListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */

@SpringBootApplication
public class ApplicationStarter {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication();
        springApplication.addListeners(new ApplicationReadyListener());
        SpringApplication.run(ApplicationStarter.class, args);
    }
}

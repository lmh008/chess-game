package com.github;

import com.github.configuration.ApplicationReadyListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/12.
 * Version v1.0
 */

@SpringBootApplication
public class ApplicationStarter extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApplicationStarter.class);
    }

    @Override
    protected WebApplicationContext run(SpringApplication application) {
        application.addListeners(new ApplicationReadyListener());
        return super.run(application);
    }

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ApplicationStarter.class);
        springApplication.addListeners(new ApplicationReadyListener());
        springApplication.run(args);
    }
}

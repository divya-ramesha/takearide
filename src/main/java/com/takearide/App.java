package com.takearide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by divya-r on 16/12/16.
 */

@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String [] args) {
        SpringApplication.run(App.class, args);
    }
}

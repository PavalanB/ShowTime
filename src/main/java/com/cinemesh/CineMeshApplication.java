package com.cinemesh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CineMeshApplication {

    public static void main(String[] args) {
        SpringApplication.run(CineMeshApplication.class, args);
    }
}

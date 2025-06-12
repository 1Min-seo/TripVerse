package com.example.easyplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TripVerseApplication {

    public static void main(String[] args) {
        SpringApplication.run(TripVerseApplication.class, args);
    }

}

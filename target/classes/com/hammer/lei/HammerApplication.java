package com.hammer.lei;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HammerApplication {

    private static final Logger logger = LoggerFactory.getLogger(HammerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HammerApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(Hammer hammer) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error(e.getMessage(), e);
//            System.exit(1);
            hammer.start();
        });
        return args -> hammer.start();
    }
}

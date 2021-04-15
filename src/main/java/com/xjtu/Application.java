package com.xjtu;

//import org.apache.xpath.operations.String;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yangkuan
 */

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
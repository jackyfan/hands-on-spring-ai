package com.jackyfan.handsonspringai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.Retryable;

@Retryable
@SpringBootApplication
public class HandsonspringaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HandsonspringaiApplication.class, args);
    }

}

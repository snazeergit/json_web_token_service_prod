package com.wipro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JwtAuthServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(JwtAuthServiceApplication.class, args);
  }
}

package com.volasoftware.tinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan({ "com.volasoftware.tinder" })
@EnableTransactionManagement
@EnableJpaRepositories
@EnableJpaAuditing
public class TinderApplication {

  public static void main(String[] args) {
    SpringApplication.run(TinderApplication.class, args);
  }

}



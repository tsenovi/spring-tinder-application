package com.volasoftware.tinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(basePackages = {"com.volasoftware.tinder"})
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
@EnableJpaAuditing
@EnableWebMvc
public class TinderApplication {

  public static void main(String[] args) {
    SpringApplication.run(TinderApplication.class, args);
  }

}



package com.volasoftware.tinder.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.volasoftware.tinder")
public class PersistenceConfig {
}

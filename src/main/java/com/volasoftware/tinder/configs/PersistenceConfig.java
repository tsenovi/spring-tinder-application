package com.volasoftware.tinder.configs;

import com.volasoftware.tinder.constants.PackageConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = PackageConstant.REPOSITORIES)
@EnableTransactionManagement
public class PersistenceConfig {

}

package com.volasoftware.tinder.configs;

import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class ApplicationConfig {

  private final AccountRepository accountRepository;

  @Bean
  public UserDetailsService userDetailsService() {
    return userName -> accountRepository.findOneByEmail(userName)
        .orElseThrow(AccountNotFoundException::new);
  }

}

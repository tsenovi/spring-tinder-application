package com.volasoftware.tinder.repositories;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.models.Account;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class AccountRepositoryTest {

  @Autowired
  private AccountRepository accountRepository;
  private static final String FIRST_NAME = "Test";
  private static final String EMAIL = "Test_Test@gmail.com";
  private static final Long ID = 1L;

  @BeforeEach
  void setUp() {
    // create a test entity
    Account account = new Account();
    account.setId(ID);
    account.setFirstName(FIRST_NAME);
    account.setLastName("Test");
    account.setEmail(EMAIL);
    account.setPassword("password");
    account.setCreatedDate(LocalDateTime.now());
    account.setLastModifiedDate(LocalDateTime.now());
    account.setGender(Gender.MALE);

    // save to repository
    accountRepository.saveAndFlush(account);
  }

  @Test
  public void testFindSavedAccountWhenGivenTheIdThenAssertDataMatches() {
    // find the entity by ID
    Optional<Account> result = accountRepository.findById(ID);

    // verify that the entity was found
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getFirstName()).isEqualTo(FIRST_NAME);
    assertThat(result.get().getEmail()).isEqualTo(EMAIL);
  }
}

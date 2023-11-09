package com.volasoftware.tinder.repositories;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.models.Account;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class AccountRepositoryTest {

  @Autowired
  private AccountRepository accountRepository;
  private final String firstName = "Test";
  private final String email = "Test_Test@gmail.com";
  private final Long id = 1L;

  @BeforeEach
  void setUp() {
    // create a test entity
    Account account = new Account();
    account.setId(id);
    account.setFirstName(firstName);
    account.setLastName("Test");
    account.setEmail(email);
    account.setPassword("password");
    account.setCreatedDate(new Date());
    account.setLastModifiedDate(new Date());
    account.setGender(Gender.MALE);

    // save to repository
    accountRepository.saveAndFlush(account);
  }

  @Test
  public void testFindSavedAccountWhenGivenTheIdThenAssertDataMatches() {
    // find the entity by ID
    Optional<Account> result = accountRepository.findById(id);

    // verify that the entity was found
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getFirstName()).isEqualTo(firstName);
    assertThat(result.get().getEmail()).isEqualTo(email);
  }
}

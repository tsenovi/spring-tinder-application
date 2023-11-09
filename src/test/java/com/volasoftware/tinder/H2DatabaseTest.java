package com.volasoftware.tinder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class H2DatabaseTest {

  @Autowired
  private AccountRepository accountRepository;

  @BeforeEach
  void setUp() {
    // create a test entity
    Account account = new Account();
    account.setId(1L);
    account.setFirstName("Test");
    account.setLastName("Test");
    account.setEmail("Test_Test@gmail.com");
    account.setPassword("password");
    account.setCreatedDate(new Date());
    account.setLastModifiedDate(new Date());
    account.setGender(Gender.MALE);

    // save to repository
    accountRepository.saveAndFlush(account);
    System.out.println("Number of accounts in the database: " + account.getEmail());
  }

  @Test
  public void testFindSavedAccountWhenGivenTheIdThenAssertDataMatches() {
    // find the entity by ID
    Optional<Account> result = accountRepository.findById(1L);

    // verify that the entity was found
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getFirstName()).isEqualTo("Test");
    assertThat(result.get().getEmail()).isEqualTo("Test_Test@gmail.com");
  }

}

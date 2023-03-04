package com.volasoftware.tinder.accounts;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.volasoftware.tinder.constants.Gender;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AccountRepositoryITests {

  @Autowired
  private AccountRepository accountRepository;

  @After
  public void tearDown() {
    accountRepository.deleteAll();
  }

  @Test
  public void testFindingAccountWhenEmailExistsThenReturnTrue() {
    Account john = new Account();
    john.setCreatedDate(Date.from(Instant.now()));
    john.setFirstName("John");
    john.setLastName("Wick");
    john.setEmail("john.wick@gmail.com");
    john.setPassword("password");
    john.setGender(Gender.MALE);

    Account savedAccount = accountRepository.save(john);
    Optional<Account> optionalAccount = accountRepository.findOneByEmail("john.wick@gmail.com");
    boolean isPresent = optionalAccount.isPresent();

    assertThat(isPresent).isTrue();
  }

  @Test
  public void testFindingAccountWhenEmailDoesNotExistsThenReturnFalse() {
    String email = "abc@gmail.com";

    Optional<Account> account = accountRepository.findOneByEmail(email);
    boolean expected = account.isPresent();

    assertThat(expected).isFalse();
  }
}
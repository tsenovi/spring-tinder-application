package com.volasoftware.tinder.repositories;

import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.VerificationToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
class VerificationTokenRepositoryTest {

  private static final String FIRST_NAME = "Test";
  private static final String EMAIL = "Test_Test@gmail.com";
  private static final Long ID = 1L;
  private static final String LAST_NAME = "Test";
  private static final String PASSWORD = "password";

  @Autowired
  VerificationTokenRepository verificationTokenRepository;

  @Autowired
  AccountRepository accountRepository;

  @AfterEach
  void tearDown() {
    verificationTokenRepository.deleteAll();
  }

  @Test
  void testGivenAccountWhenSavingHisVerificationTokenThenBothIdsMatches() {
    // given
    Account account = generateAccount();
    accountRepository.save(account);

    VerificationToken token = generateVerificationToken(account);
    verificationTokenRepository.save(token);

    VerificationToken actualToken = null;
    // when
    Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(
        token.getToken());
    if (optionalToken.isPresent()) {
      actualToken = optionalToken.get();
    }

    // then
    assert actualToken != null;
    assertEquals(account.getId(), actualToken.getAccount().getId());
  }


  private VerificationToken generateVerificationToken(
      Account account) {

    VerificationToken verificationToken = new VerificationToken();
    String randomToken = UUID.randomUUID().toString();
    verificationToken.setToken(randomToken);
    verificationToken.setAccount(account);
    verificationToken.setCreatedDate(LocalDateTime.now());
    verificationToken.setLastModifiedDate(LocalDateTime.now());
    verificationToken.setExpiresAt(LocalDateTime.now().plusDays(2));

    return verificationToken;
  }

  private Account generateAccount() {
    Account account = new Account();
    account.setId(ID);
    account.setFirstName(FIRST_NAME);
    account.setLastName(LAST_NAME);
    account.setEmail(EMAIL);
    account.setPassword(PASSWORD);
    account.setCreatedDate(LocalDateTime.now());
    account.setLastModifiedDate(LocalDateTime.now());
    account.setGender(Gender.MALE);
    return account;
  }
}
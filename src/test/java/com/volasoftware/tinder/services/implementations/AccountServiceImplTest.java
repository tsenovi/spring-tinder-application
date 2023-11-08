package com.volasoftware.tinder.services.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.dtos.RegisterDTO;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.EmailIsTakenException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.services.contracts.AccountService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

  @Mock
  private AccountRepository accountRepository;
  @Autowired
  private AccountService accountService;

  @BeforeEach
  void init() {
    accountService = new AccountServiceImpl(accountRepository);
  }


  @Test
  void testGettingAllAccountsWhenGivenListOfTwoThenExpectedTwoAccounts() {
    List<Account> accounts = getAccounts();
    given(accountRepository.findAll()).willReturn(accounts);

    List<AccountDTO> result = accountService.getAll();

    assertEquals(2, result.size());
  }

  @Test
  void testGettingAllAccountsWhenGivenListOfAccountsThenExpectedListIsNotEmpty() {
    List<Account> accounts = getAccounts();
    given(accountRepository.findAll()).willReturn(accounts);

    List<AccountDTO> result = accountService.getAll();

    assertNotNull(result);
    assertTrue(result.size() > 0);
  }

  @Test
  void testCreatingAccountWhenEmailIsNotTakenThenCreationIsSuccessful() {
    RegisterDTO registerDTO = getRegisterDTO("alex", "t", "alex@gmail.com", "password",
        Gender.MALE);

    Account account = AccountMapper.INSTANCE.registerDtoToAccount(registerDTO);

    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
    when(accountRepository.save(captor.capture())).thenReturn(account);

    AccountDTO result = accountService.save(registerDTO);

    assertEquals(result.getEmail(), registerDTO.getEmail());
  }

  @Test
  void testCreatingAccountWhenEmailIsTakenThenExceptionIsThrown() {
    RegisterDTO registerDTO = getRegisterDTO("alex", "t", "alex@gmail.com", "password",
        Gender.MALE);

    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
    when(accountRepository.save(captor.capture())).thenThrow(new EmailIsTakenException());

    Exception exception = assertThrows(EmailIsTakenException.class,
        () -> accountService.save(registerDTO));

    String expectedMessage = "Email is already taken";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  void testGettingAccountByEmailWhenGivenEmailExistsThenReturnActualAccount() {
    Account account = getAccount("alex", "t", "alex@gmail.com", "password", Gender.MALE);
    String email = "alex@gmail.com";
    given(accountRepository.findOneByEmail(email)).willReturn(Optional.of(account));

    Account accountFoundByEmail = accountService.getAccountByEmail(email);

    assertEquals(email, accountFoundByEmail.getEmail());
  }

  @Test
  void testGettingAccountByEmailWhenGivenEmailNotExistsThenExceptionIsThrown() {
    String email = "phil@gmail.com";
    given(accountRepository.findOneByEmail(email)).willThrow(new AccountNotFoundException());

    Exception exception = assertThrows(AccountNotFoundException.class,
        () -> accountService.getAccountByEmail(email));

    String expectedMessage = "Account was not found";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  private List<Account> getAccounts() {
    Account account1 = getAccount("alex", "t", "alex@gmail.com", "password", Gender.MALE);

    Account account2 = getAccount("toni", "t", "toni@gmail.com", "password", Gender.FEMALE);

    return Arrays.asList(account1, account2);
  }

  private Account getAccount(String firstName, String lastName, String email, String password,
      Gender gender) {
    Account account = new Account();
    account.setFirstName(firstName);
    account.setLastName(lastName);
    account.setEmail(email);
    account.setPassword(password);
    account.setGender(gender);

    return account;
  }

  private RegisterDTO getRegisterDTO(String firstName, String lastName, String email,
      String password, Gender gender) {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName(firstName);
    registerDTO.setLastName(lastName);
    registerDTO.setEmail(email);
    registerDTO.setPassword(password);
    registerDTO.setGender(gender);

    return registerDTO;
  }
}
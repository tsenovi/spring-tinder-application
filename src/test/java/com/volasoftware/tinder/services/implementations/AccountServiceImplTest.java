package com.volasoftware.tinder.services.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.EmailIsTakenException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Autowired
    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void testGettingAllAccountsWhenGivenListOfTwoThenExpectedTwoAccounts() {
        List<Account> accounts = getAccounts();
        given(accountRepository.findAll()).willReturn(accounts);
        List<AccountDto> result = accountService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void testGettingAllAccountsWhenGivenListOfAccountsThenExpectedListIsNotEmpty() {
        List<Account> accounts = getAccounts();
        given(accountRepository.findAll()).willReturn(accounts);

        List<AccountDto> result = accountService.getAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testCreatingAccountWhenEmailIsNotTakenThenCreationIsSuccessful() {
        RegisterRequest registerRequest = getRegisterRequest("alex", "t", "alex@gmail.com", "password",
                Gender.MALE);

        Account account = AccountMapper.INSTANCE.registerRequestToAccount(registerRequest);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        when(accountRepository.save(captor.capture())).thenReturn(account);

        AccountDto result = accountService.register(registerRequest);

        assertEquals(result.getEmail(), registerRequest.getEmail());
    }

    @Test
    void testCreatingAccountWhenEmailIsTakenThenExceptionIsThrown() {
        RegisterRequest registerRequest = getRegisterRequest("alex", "t", "alex@gmail.com", "password",
                Gender.MALE);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        when(accountRepository.save(captor.capture())).thenThrow(new EmailIsTakenException());

        Exception exception = assertThrows(EmailIsTakenException.class,
                () -> accountService.register(registerRequest));

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

    private RegisterRequest getRegisterRequest(String firstName, String lastName, String email,
                                               String password, Gender gender) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName(firstName);
        registerRequest.setLastName(lastName);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setGender(gender);

        return registerRequest;
    }
}
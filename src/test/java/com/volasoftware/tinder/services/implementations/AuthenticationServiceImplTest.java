package com.volasoftware.tinder.services.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.constants.MailConstant;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.EmailDto;
import com.volasoftware.tinder.dtos.LoginRequest;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.AccountNotVerifiedException;
import com.volasoftware.tinder.exceptions.EmailAlreadyVerifiedException;
import com.volasoftware.tinder.exceptions.EmailIsTakenException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.VerificationToken;
import com.volasoftware.tinder.repositories.AccountRepository;

import com.volasoftware.tinder.services.contracts.AccountService;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.volasoftware.tinder.repositories.VerificationTokenRepository;
import com.volasoftware.tinder.responses.LoginResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AuthenticationServiceImplTest {

    public static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(
        2023,
        Month.DECEMBER,
        7,
        12,
        30,
        00,
        50000);
    private static final PageRequest pageRequest = PageRequest.of(0, 5);
    private static final String FIRST_NAME = "Test";
    private static final String EMAIL = "Test_Test@gmail.com";
    private static final String WRONG_EMAIL = "XXXXXXXXXXXXXXXXXXX";
    private static final Long ID = 1L;
    private static final String LAST_NAME = "Test";
    private static final String PASSWORD = "password";
    private static final String WRONG_PASSWORD = "XXXXXXXXXXXXXXXXXXXXX";

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private VerificationTokenRepository verificationTokenRepository;

    @MockBean
    private AccountService accountService;

    @MockBean
    private VerificationTokenServiceImpl verificationTokenService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Autowired
    private AccountMapper accountMapper;

    @Test
    void testGettingAllAccountsWhenGivenListOfTwoThenExpectedTwoAccounts() {
        List<Account> accounts = getAccounts();
        Page<Account> accountsPage = new PageImpl<>(accounts);
        when(accountRepository.findAll(pageRequest)).thenReturn(accountsPage);
        List<AccountDto> result = accountService.getAccounts(pageRequest);

        assertEquals(2, result.size());
    }

    @Test
    void testGettingAllAccountsWhenGivenListOfAccountsThenExpectedListIsNotEmpty() {
        List<Account> accounts = getAccounts();
        Page<Account> accountsPage = new PageImpl<>(accounts);
        when(accountRepository.findAll(pageRequest)).thenReturn(accountsPage);

        List<AccountDto> result = accountService.getAccounts(pageRequest);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testCreatingAccountWhenEmailIsNotTakenThenCreationIsSuccessful() {
        RegisterRequest registerRequest = getRegisterRequest(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD,
            Gender.MALE);
        Account account = generateAccount();
        VerificationToken verificationToken = generateVerificationToken(account);

        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(
            verificationToken);

        AccountDto result = authenticationService.register(registerRequest);

        assertEquals(result.getEmail(), registerRequest.getEmail());
    }

    @Test
    void testCreatingAccountWhenEmailIsTakenThenExceptionIsThrown() {
        RegisterRequest registerRequest = getRegisterRequest("alex", "t", "alex@gmail.com",
            "password",
            Gender.MALE);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        when(accountRepository.save(captor.capture())).thenThrow(
            new EmailIsTakenException(MailConstant.ALREADY_TAKEN));

        Exception exception = assertThrows(EmailIsTakenException.class,
            () -> authenticationService.register(registerRequest));

        String expectedMessage = MailConstant.ALREADY_TAKEN;
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    void testGettingAccountByEmailWhenGivenEmailExistsThenReturnActualAccount() {
        Account account = getAccount("alex", "t", "alex@gmail.com", "password", Gender.MALE);
        String email = "alex@gmail.com";
        given(accountRepository.findOneByEmail(email)).willReturn(Optional.of(account));

        AccountDto accountFoundByEmail = accountService.getAccountDtoByEmail(email);

        assertEquals(email, accountFoundByEmail.getEmail());
    }

    @Test
    void testGettingAccountByEmailWhenGivenEmailNotExistsThenExceptionIsThrown() {
        String email = "phil@gmail.com";
        given(accountRepository.findOneByEmail(email)).willThrow(
            new AccountNotFoundException("Account was not found"));

        Exception exception = assertThrows(AccountNotFoundException.class,
            () -> accountService.getAccountDtoByEmail(email));

        String expectedMessage = "Account was not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testLoginWhenAccountNotFoundThenExceptionThrown() {
        LoginRequest loginRequest = generateLoginRequest(null, null);

        assertThrows(AccountNotFoundException.class,
            () -> authenticationService.login(loginRequest));
    }

    @Test
    void testLoginWhenCredentialsAreWrongThenExceptionThrown() {
        LoginRequest loginRequest = generateLoginRequest(WRONG_EMAIL, WRONG_PASSWORD);
        Account account = generateAccount();
        account.setVerified(true);

        given(accountRepository.findOneByEmail(account.getEmail())).willReturn(
            Optional.of(account));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException(AccountConstant.WRONG_PASSWORD));

        assertThrows(BadCredentialsException.class,
            () -> authenticationService.login(loginRequest));
    }

    @Test
    void testLoginWhenAccountNotVerifiedThenExceptionThrown() {
        LoginRequest loginRequest = generateLoginRequest(EMAIL, PASSWORD);
        Account account = generateAccount();

        given(accountRepository.findOneByEmail(account.getEmail())).willReturn(
            Optional.of(account));

        assertThrows(AccountNotVerifiedException.class,
            () -> authenticationService.login(loginRequest));
    }

    @Test
    void testLoginWhenAccountVerifiedThenSuccessOperation() {
        LoginRequest loginRequest = generateLoginRequest(EMAIL, PASSWORD);
        Account account = generateAccount();
        account.setVerified(true);

        given(accountRepository.findOneByEmail(account.getEmail())).willReturn(
            Optional.of(account));

        LoginResponse loginResponse = authenticationService.login(loginRequest);

        assertNotNull(loginResponse.getToken());
    }

    @Test
    void testReverifyWhenAccountNotVerifiedThenSuccessfulOperation() {
        //given
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail(EMAIL);

        Account account = new Account();
        account.setId(1L);
        account.setEmail(EMAIL);
        account.setVerified(false);

        when(accountRepository.findOneByEmail(EMAIL)).thenReturn(Optional.of(account));
        VerificationToken verificationToken = new VerificationToken();
        when(verificationTokenService.getVerificationToken(account)).thenReturn(verificationToken);

        //when
        EmailDto resultDto = authenticationService.resendVerification(emailDto);

        //then
        assertNotNull(resultDto);
        assertEquals(EMAIL, resultDto.getEmail());
    }

    @Test
    void testReverifyWhenAccountVerifiedThenExceptionThrown() {
        //given
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail(EMAIL);

        Account account = new Account();
        account.setId(1L);
        account.setEmail(EMAIL);
        account.setVerified(true);

        when(accountRepository.findOneByEmail(EMAIL)).thenReturn(Optional.of(account));

        //when
        Exception exception = assertThrows(EmailAlreadyVerifiedException.class,
            () -> authenticationService.resendVerification(emailDto));

        //then
        String expectedMessage = MailConstant.ALREADY_CONFIRMED;
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, expectedMessage);
    }

    private LoginRequest generateLoginRequest(String email, String password) {
        return new LoginRequest(email, password);
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

    private VerificationToken generateVerificationToken(Account account) {
        VerificationToken verificationToken = new VerificationToken();
        String randomToken = UUID.randomUUID().toString();
        verificationToken.setToken(randomToken);
        verificationToken.setAccount(account);
        verificationToken.setCreatedDate(LOCAL_DATE_TIME);
        verificationToken.setLastModifiedDate(LOCAL_DATE_TIME);
        verificationToken.setExpiresAt(LOCAL_DATE_TIME.plusDays(2));

        return verificationToken;
    }

    private Account generateAccount() {
        Account account = new Account();
        account.setId(ID);
        account.setFirstName(FIRST_NAME);
        account.setLastName(LAST_NAME);
        account.setEmail(EMAIL);
        account.setPassword(PASSWORD);
        account.setCreatedDate(LOCAL_DATE_TIME);
        account.setLastModifiedDate(LOCAL_DATE_TIME);
        account.setGender(Gender.MALE);
        return account;
    }
}
package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.exceptions.EmailAlreadyVerifiedException;
import com.volasoftware.tinder.exceptions.VerificationTokenExpiredException;
import com.volasoftware.tinder.exceptions.VerificationTokenNotExistException;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.VerificationToken;
import com.volasoftware.tinder.repositories.VerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class VerificationTokenServiceImplTest {

    private static final String FIRST_NAME = "Test";
    private static final String EMAIL = "Test_Test@gmail.com";
    private static final Long ID = 1L;
    private static final String LAST_NAME = "Test";
    private static final String PASSWORD = "password";

    @Mock
    VerificationTokenRepository repository;

    @Autowired
    @InjectMocks
    VerificationTokenServiceImpl service;

    @Test
    void testVerifyTokenWhenTokenExpiredThenThrowVerificationTokenExpiredException() {
        //given
        Account account = generateAccount();
        VerificationToken verificationToken = generateVerificationToken(account);
        String uuidToken = verificationToken.getToken();
        verificationToken.setExpiresAt(LocalDateTime.now().minusDays(1));

        //when
        when(repository.findByToken(uuidToken)).thenReturn(Optional.of(verificationToken));

        //then
        VerificationTokenExpiredException exception = assertThrows(
                VerificationTokenExpiredException.class, () -> {
                    service.verifyToken(uuidToken);
                });

        assertEquals("Token expired!", exception.getMessage());
    }

    @Test
    void testVerifyTokenWhenEmailAlreadyVerifiedThenThrowEmailAlreadyVerifiedException() {
        //given
        Account account = generateAccount();
        VerificationToken verificationToken = generateVerificationToken(account);
        String uuidToken = verificationToken.getToken();
        verificationToken.setVerifiedAt(LocalDateTime.now().plusMinutes(5));

        //when
        when(repository.findByToken(uuidToken)).thenReturn(Optional.of(verificationToken));

        //then
        EmailAlreadyVerifiedException exception = assertThrows(
                EmailAlreadyVerifiedException.class, () -> {
                    service.verifyToken(uuidToken);
                });

        assertEquals("Email already confirmed!", exception.getMessage());
    }

    @Test
    void testVerifyTokenWhenTokenDoesNotExistThenThrowTokenNotExistsException() {
        //given
        String token = "tokenDoesNotExist";

        //when
        when(repository.findByToken(token)).thenReturn(Optional.empty());

        //then
        VerificationTokenNotExistException exception = assertThrows(
                VerificationTokenNotExistException.class, () -> {
                    service.verifyToken(token);
                });

        assertEquals("Token not exist!", exception.getMessage());
    }

    @Test
    void testVerifyTokenWhenTokenExistThenReturnTheAccountDto() {
        //given
        Account account = generateAccount();
        VerificationToken verificationToken = generateVerificationToken(account);

        //when
        when(repository.findByToken(verificationToken.getToken())).thenReturn(Optional.of(verificationToken));
        when(repository.save(verificationToken)).thenReturn(verificationToken);

        Account expectedAccount = service.verifyToken(verificationToken.getToken());

        //then
        verify(repository, times(1)).save(verificationToken);

        assertNotNull(expectedAccount);
        assertEquals(account.getEmail(), expectedAccount.getEmail());
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
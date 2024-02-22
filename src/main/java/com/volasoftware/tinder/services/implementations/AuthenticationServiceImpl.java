package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.*;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.EmailDto;
import com.volasoftware.tinder.dtos.LoginRequest;
import com.volasoftware.tinder.exceptions.*;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.VerificationToken;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.responses.LoginResponse;
import com.volasoftware.tinder.services.contracts.*;

import java.security.Principal;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final VerificationTokenService verificationTokenService;

    private final EmailService emailService;

    private final EmailValidator emailValidator;

    private final AccountService accountService;

    private final AccountMapper accountMapper;

    private final FileService fileService;

    private final AuthenticationManager authenticationManager;

    @Value("${server.base-url}")
    private String baseUrl;

    @Value("${server.verify-url}")
    private String verifyUrl;

    @Override
    public AccountDto register(RegisterRequest registerRequest) {
        checkIfEmailIsValid(registerRequest.getEmail());
        accountService.checkIfEmailIsTaken(registerRequest.getEmail());

        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Account account = accountMapper.registerRequestToAccount(registerRequest);
        account.setAccountType(AccountType.REAL);
        account.setRole(Role.USER);
        account.setLocked(false);
        account.setVerified(false);
        Account savedAccount = accountService.updateAccount(account);

        VerificationToken verificationToken = verificationTokenService.generateToken(savedAccount);
        sendVerificationMail(registerRequest.getEmail(), verificationToken.getToken());

        return accountMapper.accountToAccountDto(savedAccount);
    }

    @Override
    public AccountDto verifyAccount(String token) {
        Account account = verificationTokenService.verifyToken(token);
        account.setVerified(true);
        Account updatedAccount = accountService.updateAccount(account);

        return accountMapper.accountToAccountDto(updatedAccount);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        Account account = accountService.getAccountByEmail(loginRequest.getEmail());

        if (!account.isVerified()) {
            throw new AccountNotVerifiedException(AccountConstant.NOT_VERIFIED);
        }

        return new LoginResponse(jwtService.generateToken(account));
    }

    @Override
    public AccountDto updateAccount(AccountDto accountDto, Principal principal) {
        Account account = accountService.getAccountByEmail(accountDto.getEmail());
        if (!account.getEmail().equals(principal.getName())) {
            throw new AccountNotOwnerException(AccountConstant.NOT_OWNER);
        }

        account.setFirstName(accountDto.getFirstName());
        account.setLastName(accountDto.getLastName());
        account.setEmail(accountDto.getEmail());
        account.setGender(accountDto.getGender());

        return accountMapper.accountToAccountDto(accountService.updateAccount(account));
    }

    @Override
    public AccountDto recoverPassword(Principal principal) {
        Account account = accountService.getAccountByEmail(principal.getName());
        String randomPassword = UUID.randomUUID().toString();

        try {
            sendRecoveredPasswordMail(account.getEmail(), randomPassword);
        } catch (Exception e) {
            throw new EmailIsNotValidException(MailConstant.NOT_VALID);
        }

        account.setPassword(passwordEncoder.encode(randomPassword));
        Account savedAccount = accountService.updateAccount(account);

        return accountMapper.accountToAccountDto(savedAccount);
    }

    @Override
    public EmailDto resendVerification(EmailDto emailDto) {
        Account account = accountService.getAccountByEmail(emailDto.getEmail());
        if (account.isVerified()) {
            throw new EmailAlreadyVerifiedException(MailConstant.ALREADY_CONFIRMED);
        }

        VerificationToken verificationToken = verificationTokenService.getVerificationToken(
            account);
        sendVerificationMail(emailDto.getEmail(), verificationToken.getToken());

        return emailDto;
    }

    private void sendRecoveredPasswordMail(String receiver, String recoveredPassword) {
        byte[] contentBytes = fileService.readHtml(FilePathConstant.PASSWORD_RECOVERY_EMAIL_HTML);
        String content = new String(contentBytes).replace(
            HtmlConstant.PASSWORD_ELEMENT_VALUE,
            recoveredPassword
        );

        emailService.send(receiver, content);
    }

    private void sendVerificationMail(String receiver, String token) {
        String link = baseUrl + verifyUrl + token;
        byte[] contentBytes = fileService.readHtml(FilePathConstant.VERIFICATION_EMAIL_HTML);
        String content = new String(contentBytes).replace(HtmlConstant.HREF, link);

        emailService.send(receiver, content);
    }

    private void checkIfEmailIsValid(String email) {
        boolean isValidEmail = emailValidator.test(email);

        if (!isValidEmail) {
            throw new EmailIsNotValidException(MailConstant.NOT_VALID);
        }
    }
}

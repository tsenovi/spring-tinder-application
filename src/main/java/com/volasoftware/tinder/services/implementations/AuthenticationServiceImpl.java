package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.*;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.LoginRequest;
import com.volasoftware.tinder.exceptions.*;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.VerificationToken;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.responses.LoginResponse;
import com.volasoftware.tinder.services.contracts.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    private final FileService fileService;

    @Value("${server.base-url}")
    private String baseUrl;

    @Value("${server.verify-url}")
    private String verifyUrl;

    @Override
    public List<AccountDto> getAll() {
        List<Account> accounts = accountRepository.findAll();

        return accountMapper.accountListToAccountDtoList(accounts);
    }

    @Override
    public AccountDto register(RegisterRequest registerRequest) {
        checkIfEmailIsValid(registerRequest.getEmail());
        checkIfEmailIsTaken(registerRequest.getEmail());

        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Account account = accountMapper.registerRequestToAccount(registerRequest);
        account.setRole(Role.USER);
        account.setLocked(false);
        account.setVerified(false);
        Account savedAccount = accountRepository.save(account);

        VerificationToken verificationToken = verificationTokenService.generateToken(savedAccount);
        sendVerificationMail(registerRequest.getEmail(), verificationToken.getToken());

        return accountMapper.accountToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountByEmail(String email) {
        Account account = accountRepository.findOneByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException(AccountConstant.NOT_FOUND));

        return accountMapper.accountToAccountDto(account);
    }

    @Override
    public AccountDto verifyAccount(String token) {
        Account account = verificationTokenService.verifyToken(token);
        account.setVerified(true);
        Account updatedAccount = accountRepository.save(account);

        return accountMapper.accountToAccountDto(updatedAccount);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Account account = accountRepository.findOneByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AccountNotFoundException(AccountConstant.NOT_FOUND));

        if (!isValidPassword(loginRequest.getPassword(), account.getPassword())) {
            throw new PasswordMismatchException(AccountConstant.WRONG_PASSWORD);
        }

        if (!account.isVerified()) {
            throw new AccountNotVerifiedException(AccountConstant.NOT_VERIFIED);
        }

        return new LoginResponse(jwtService.generateToken(account));
    }

    private boolean isValidPassword(String requestPassword, String accountPassword) {
        return passwordEncoder.matches(requestPassword, accountPassword);
    }

    private void sendVerificationMail(String receiver, String token) {
        String link = baseUrl + verifyUrl + token;
        byte[] contentBytes = fileService.readHtml(FilePathConstant.VERIFICATION_EMAIL_HTML);
        String content = new String(contentBytes).replace(HtmlConstant.HREF, link);

        emailService.send(receiver, content);
    }

    private void checkIfEmailIsTaken(String email) {
        Optional<Account> optionalAccount = accountRepository.findOneByEmail(email);
        if (optionalAccount.isPresent()) {
            throw new EmailIsTakenException(MailConstant.ALREADY_TAKEN);
        }
    }

    private void checkIfEmailIsValid(String email) {
        boolean isValidEmail = emailValidator.test(email);

        if (!isValidEmail) {
            throw new EmailIsNotValidException(MailConstant.NOT_VALID);
        }
    }
}

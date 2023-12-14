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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    private final FileService fileService;

    private final AuthenticationManager authenticationManager;

    @Value("${server.base-url}")
    private String baseUrl;

    @Value("${server.verify-url}")
    private String verifyUrl;

    @Override
    public List<AccountDto> getAccounts(Pageable pageable) {
        Page<Account> accountsPage = accountRepository.findAll(pageable);
        List<Account> accountsList = new ArrayList<>();

        while (!accountsPage.isEmpty()) {
            accountsPage.forEach(accountsList::add);

            accountsPage = accountRepository.findAll(pageable.next());
        }

        return accountMapper.accountListToAccountDtoList(accountsList);
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
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        Account account = accountRepository.findOneByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new AccountNotFoundException(AccountConstant.NOT_FOUND));

        if (!account.isVerified()) {
            throw new AccountNotVerifiedException(AccountConstant.NOT_VERIFIED);
        }

        return new LoginResponse(jwtService.generateToken(account));
    }

    @Override
    public AccountDto updateAccount(AccountDto accountDto, Principal principal) {
        Account account = findAccountByEmail(accountDto.getEmail());
        if (!account.getEmail().equals(principal.getName())) {
            throw new AccountNotOwnerException(AccountConstant.NOT_OWNER);
        }

        account.setFirstName(accountDto.getFirstName());
        account.setLastName(accountDto.getLastName());
        account.setEmail(accountDto.getEmail());
        account.setGender(accountDto.getGender());

        return accountMapper.accountToAccountDto(accountRepository.save(account));
    }

    @Override
    public AccountDto recoverPassword(Principal principal) {
        Account account = findAccountByEmail(principal.getName());
        String randomPassword = UUID.randomUUID().toString();

        try {
            sendRecoveredPasswordMail(account.getEmail(), randomPassword);
        } catch (Exception e) {
            throw new EmailIsNotValidException(MailConstant.NOT_VALID);
        }

        account.setPassword(passwordEncoder.encode(randomPassword));
        accountRepository.save(account);

        return accountMapper.accountToAccountDto(account);
    }

    private void sendRecoveredPasswordMail(String receiver, String recoveredPassword) {
        byte[] contentBytes = fileService.readHtml(FilePathConstant.PASSWORD_RECOVERY_EMAIL_HTML);
        String content = new String(contentBytes).replace(HtmlConstant.GENERATED_PASSWORD, recoveredPassword);

        emailService.send(receiver, content);
    }

    private Account findAccountByEmail(String email) {
        return accountRepository.findOneByEmail(email)
            .orElseThrow(() -> new AccountNotFoundException(AccountConstant.NOT_FOUND));
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

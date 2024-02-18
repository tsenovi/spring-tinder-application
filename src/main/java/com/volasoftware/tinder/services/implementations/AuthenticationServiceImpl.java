package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.*;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.EmailDto;
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

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public AccountDto getAccountByEmail(String email) {
        Account account = findAccountByEmail(email);
        return accountMapper.accountToAccountDto(account);
    }

    @Override
    public List<Account> getFriendsByLocation(Account loggedAccount, Double accountLatitude,
        Double accountLongitude) {
        return accountRepository.findFriendsByLocation(
            loggedAccount.getId(), accountLatitude, accountLongitude);
    }

    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow(
            () -> new AccountNotFoundException(AccountConstant.NOT_FOUND)
        );
    }

    @Override
    public Account getLoggedAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return findAccountByEmail(username);
    }

    @Override
    public Set<Account> getAccountsByType(AccountType accountType, Pageable pageable) {

        return Stream.iterate(pageable, Pageable::next)
            .map(pageRequest -> accountRepository.findByAccountType(accountType, pageRequest))
            .takeWhile(page -> page != null && page.hasContent())
            .flatMap(page -> page.getContent().stream())
            .collect(Collectors.toSet());
    }

    @Override
    public AccountDto register(RegisterRequest registerRequest) {
        checkIfEmailIsValid(registerRequest.getEmail());
        checkIfEmailIsTaken(registerRequest.getEmail());

        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Account account = accountMapper.registerRequestToAccount(registerRequest);
        account.setAccountType(AccountType.REAL);
        account.setRole(Role.USER);
        account.setLocked(false);
        account.setVerified(false);
        Account savedAccount = updateAccount(account);

        VerificationToken verificationToken = verificationTokenService.generateToken(savedAccount);
        sendVerificationMail(registerRequest.getEmail(), verificationToken.getToken());

        return accountMapper.accountToAccountDto(savedAccount);
    }

    @Override
    public AccountDto verifyAccount(String token) {
        Account account = verificationTokenService.verifyToken(token);
        account.setVerified(true);
        Account updatedAccount = updateAccount(account);

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

        Account account = findAccountByEmail(loginRequest.getEmail());

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

        return accountMapper.accountToAccountDto(updateAccount(account));
    }

    @Override
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
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
        Account savedAccount = updateAccount(account);

        return accountMapper.accountToAccountDto(savedAccount);
    }

    @Override
    public EmailDto resendVerification(EmailDto emailDto) {
        Account account = findAccountByEmail(emailDto.getEmail());
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

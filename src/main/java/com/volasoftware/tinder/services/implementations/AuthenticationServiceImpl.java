package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.Role;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.exceptions.EmailIsNotValidException;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.VerificationToken;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.EmailIsTakenException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.services.contracts.AuthenticationService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.volasoftware.tinder.services.contracts.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private EmailValidator emailValidator;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<AccountDto> getAll() {
        List<Account> accounts = accountRepository.findAll();

        return AccountMapper.INSTANCE.accountListToAccountDtoList(accounts);
    }

    @Override
    public AccountDto register(RegisterRequest registerRequest) {
        checkIfEmailIsValid(registerRequest.getEmail());
        checkIfEmailIsTaken(registerRequest.getEmail());

        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Account account = AccountMapper.INSTANCE.registerRequestToAccount(registerRequest);
        account.setRole(Role.USER);
        account.setLocked(false);
        account.setVerified(false);
        Account savedAccount = accountRepository.save(account);

        verificationTokenService.register(savedAccount);

        return AccountMapper.INSTANCE.accountToAccountDto(savedAccount);
    }


    @Override
    public Account getAccountByEmail(String email) {
        return accountRepository.findOneByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));
    }

    private void checkIfEmailIsTaken(String email) {
        Optional<Account> optionalAccount = accountRepository.findOneByEmail(email);
        if (optionalAccount.isPresent()) {
            throw new EmailIsTakenException("Email is already taken!");
        }
    }

    private void checkIfEmailIsValid(String email) {
        boolean isValidEmail = emailValidator.test(email);

        if (!isValidEmail) {
            throw new EmailIsNotValidException("Email is not valid!");
        }
    }
}

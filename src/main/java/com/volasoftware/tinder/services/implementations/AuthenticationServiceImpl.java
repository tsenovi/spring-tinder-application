package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.constants.MailConstant;
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
import com.volasoftware.tinder.services.contracts.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

        emailService.sendVerificationMail(
                registerRequest.getEmail(),
                verificationToken.getToken()
        );

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

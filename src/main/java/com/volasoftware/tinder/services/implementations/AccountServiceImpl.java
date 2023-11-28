package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.Role;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.EmailIsTakenException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.services.contracts.AccountService;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Override
    public List<AccountDto> getAll() {
        List<Account> accounts = accountRepository.findAll();

        return AccountMapper.INSTANCE.accountListToAccountDtoList(accounts);
    }

    @Override
    public AccountDto register(RegisterRequest registerRequest) {
        checkIfEmailIsTaken(registerRequest.getEmail());

        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Account account = AccountMapper.INSTANCE.registerRequestToAccount(registerRequest);
        account.setRole(Role.USER);
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.INSTANCE.accountToAccountDto(savedAccount);
    }

    @Override
    public Account getAccountByEmail(String email) {
        return accountRepository.findOneByEmail(email).orElseThrow(AccountNotFoundException::new);
    }

    private void checkIfEmailIsTaken(String email) {
        Optional<Account> optionalAccount = accountRepository.findOneByEmail(email);
        if (optionalAccount.isPresent()) {
            throw new EmailIsTakenException();
        }
    }
}

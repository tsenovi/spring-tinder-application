package com.volasoftware.tinder.services;

import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.dtos.RegisterDTO;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.EmailIsTakenException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.dtos.ResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;

  @Override
  public List<Account> getAll() {
    return accountRepository.findAll();
  }

  @Override
  public ResponseDTO<?> save(RegisterDTO registerDTO) {
    checkIfEmailIsTaken(registerDTO.getEmail());
    Account account = AccountMapper.INSTANCE.registerDtoToAccount(registerDTO);
    Account savedAccount = accountRepository.save(account);
    AccountDTO accountDTO = AccountMapper.INSTANCE.accountToAccountDto(savedAccount);

    return ResponseDTO
        .builder()
        .data(accountDTO)
        .message("Register Complete!")
        .status(HttpStatus.CREATED)
        .timestamp(LocalDateTime.now())
        .build();
  }

  @Override
  public Account findByEmailIfExists(String email) {
    return accountRepository.findOneByEmail(email).orElseThrow(AccountNotFoundException::new);
  }

  private void checkIfEmailIsTaken(String email) {
    Optional<Account> optionalAccount = accountRepository.findOneByEmail(email);
    if (optionalAccount.isPresent()) {
      throw new EmailIsTakenException();
    }
  }
}

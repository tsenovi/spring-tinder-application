package com.volasoftware.tinder.accounts;

import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.dtos.RegisterDTO;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.responses.Response;
import java.time.Instant;
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
  public Response<?> save(RegisterDTO registerDTO) {
    Account accountToSave = AccountMapper.INSTANCE.registerDtoToAccount(registerDTO);
    Account savedAccount = accountRepository.save(accountToSave);
    AccountDTO accountDTO = AccountMapper.INSTANCE.accountToAccountDto(savedAccount);

    return Response.builder()
        .data(accountDTO)
        .message("Register Complete!")
        .status(HttpStatus.CREATED)
        .timestamp(Instant.now())
        .build();
  }

  @Override
  public Optional<Account> findByEmail(String email) {
    return accountRepository.findOneByEmail(email);
  }
}

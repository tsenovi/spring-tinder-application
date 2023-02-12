package com.volasoftware.tinder.accounts;

import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.dtos.RegisterDTO;
import com.volasoftware.tinder.mapper.AccountMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
  public AccountDTO save(RegisterDTO registerDTO) {
    Account accountToSave = AccountMapper.INSTANCE.registerDtoToAccount(registerDTO);

    Account savedAccount = accountRepository.save(accountToSave);

    return AccountMapper.INSTANCE.accountToAccountDto(savedAccount);
  }

  @Override
  public Optional<Account> findByEmail(String email) {
    return accountRepository.findOneByEmail(email);
  }
}

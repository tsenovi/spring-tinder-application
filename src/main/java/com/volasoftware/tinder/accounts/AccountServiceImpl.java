package com.volasoftware.tinder.accounts;

import com.volasoftware.tinder.dtos.RegisterDTO;
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
  public Account save(RegisterDTO registerDTO) {
    Account account = new Account();
    account.setFirstName(registerDTO.getFirstName());
    account.setLastName(registerDTO.getLastName());
    account.setEmail(registerDTO.getEmail());
    account.setPassword(registerDTO.getPassword());
    account.setGender(registerDTO.getGender());
    return accountRepository.save(account);
  }

  @Override
  public Optional<Account> findByEmail(String email) {
    return accountRepository.findOneByEmail(email);
  }
}

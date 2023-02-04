package com.volasoftware.tinder.accounts;

import com.volasoftware.tinder.constants.Gender;
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
  public Account save(AccountRegisterDTO accountRegisterDTO) {
    Account account = new Account();
    account.setFirstName(accountRegisterDTO.getFirstName());
    account.setLastName(accountRegisterDTO.getLastName());
    account.setEmail(accountRegisterDTO.getEmail());
    account.setPassword(accountRegisterDTO.getPassword());
    if(accountRegisterDTO.getGender() == Gender.FEMALE.toString()){
      account.setGender(Gender.FEMALE);
    } else {
      account.setGender(Gender.MALE);
    }

    return accountRepository.save(account);
  }

  @Override
  public Optional<Account> findByEmail(String email) {
    return accountRepository.findOneByEmail(email);
  }
}

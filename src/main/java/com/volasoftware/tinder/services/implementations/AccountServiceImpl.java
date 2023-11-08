package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.dtos.RegisterDTO;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.EmailIsTakenException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.services.contracts.AccountService;
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
  public List<AccountDTO> getAll() {
    List<Account> accounts = accountRepository.findAll();

    return AccountMapper.INSTANCE.accountListToAccountDtoList(accounts);
  }

  @Override
  public AccountDTO save(RegisterDTO registerDTO) {
    checkIfEmailIsTaken(registerDTO.getEmail());
    Account account = AccountMapper.INSTANCE.registerDtoToAccount(registerDTO);
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

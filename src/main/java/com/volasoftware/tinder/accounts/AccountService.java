package com.volasoftware.tinder.accounts;

import java.util.List;
import java.util.Optional;


public interface AccountService {

  List<Account> getAll();

  Account save(AccountRegisterDTO account);

  Optional<Account> findByEmail(String email);
}

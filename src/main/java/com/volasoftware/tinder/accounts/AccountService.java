package com.volasoftware.tinder.accounts;

import com.volasoftware.tinder.dtos.RegisterDTO;
import java.util.List;
import java.util.Optional;


public interface AccountService {

  List<Account> getAll();

  Account save(RegisterDTO account);

  Optional<Account> findByEmail(String email);
}

package com.volasoftware.tinder.accounts;

import com.volasoftware.tinder.dtos.RegisterDTO;
import com.volasoftware.tinder.responses.Response;
import java.util.List;
import java.util.Optional;


public interface AccountService {

  List<Account> getAll();

  Response<?> save(RegisterDTO account);

  Optional<Account> findByEmail(String email);
}

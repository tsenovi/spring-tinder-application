package com.volasoftware.tinder.services;

import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.dtos.RegisterDTO;
import com.volasoftware.tinder.dtos.ResponseDTO;
import java.util.List;


public interface AccountService {

  List<Account> getAll();

  ResponseDTO<?> save(RegisterDTO account);

  Account findByEmailIfExists(String email);
}

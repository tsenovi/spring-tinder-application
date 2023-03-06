package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.dtos.RegisterDTO;
import com.volasoftware.tinder.dtos.ResponseDTO;
import java.util.List;


public interface AccountService {

  List<AccountDTO> getAll();

  ResponseDTO<?> save(RegisterDTO account);

  Account getAccountByEmail(String email);
}

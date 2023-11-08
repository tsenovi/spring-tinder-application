package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.dtos.RegisterDTO;
import java.util.List;


public interface AccountService {

  List<AccountDTO> getAll();

  AccountDTO save(RegisterDTO account);

  Account getAccountByEmail(String email);
}

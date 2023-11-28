package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.dtos.RegisterRequest;
import java.util.List;


public interface AccountService {

  List<AccountDTO> getAll();

  AccountDTO register(RegisterRequest registerRequest);

  Account getAccountByEmail(String email);
}

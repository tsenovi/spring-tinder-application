package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.dtos.RegisterRequest;
import java.util.List;


public interface AccountService {

  List<AccountDto> getAll();

  AccountDto register(RegisterRequest registerRequest);

  Account getAccountByEmail(String email);
}

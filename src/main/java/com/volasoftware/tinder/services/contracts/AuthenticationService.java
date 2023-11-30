package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.RegisterRequest;

import java.util.List;


public interface AuthenticationService {

    List<AccountDto> getAll();

    AccountDto register(RegisterRequest registerRequest);

    AccountDto getAccountByEmail(String email);

    String verify(String token);
}

package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.LoginRequest;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.responses.LoginResponse;

import java.util.List;


public interface AuthenticationService {

    List<AccountDto> getAll();

    AccountDto register(RegisterRequest registerRequest);

    AccountDto getAccountByEmail(String email);

    AccountDto verifyAccount(String token);

    LoginResponse login(LoginRequest loginRequest);
}

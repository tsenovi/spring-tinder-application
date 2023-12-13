package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.LoginRequest;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.responses.LoginResponse;

import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.Pageable;


public interface AuthenticationService {

  List<AccountDto> getAccounts(Pageable pageable);

  AccountDto register(RegisterRequest registerRequest);

  AccountDto getAccountByEmail(String email);

  AccountDto verifyAccount(String token);

  LoginResponse login(LoginRequest loginRequest);

  AccountDto updateAccount(AccountDto accountDto, Principal principal);
}

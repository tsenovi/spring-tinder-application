package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.EmailDto;
import com.volasoftware.tinder.dtos.LoginRequest;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.responses.LoginResponse;

import java.security.Principal;


public interface AuthenticationService {

    AccountDto register(RegisterRequest registerRequest);

    AccountDto verifyAccount(String token);

    LoginResponse login(LoginRequest loginRequest);

    AccountDto updateAccount(AccountDto accountDto, Principal principal);

    AccountDto recoverPassword(Principal principal);

    EmailDto resendVerification(EmailDto emailDto);
}

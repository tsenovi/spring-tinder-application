package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.constants.AccountType;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.dtos.EmailDto;
import com.volasoftware.tinder.dtos.LoginRequest;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.responses.LoginResponse;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;


public interface AuthenticationService {

    List<AccountDto> getAccounts(Pageable pageable);

    List<Account> getFriendsByLocation(Account loggedAccount, Double accountLatitude,
        Double accountLongitude);

    Account getAccountById(Long id);

    Account getLoggedAccount();

    Set<Account> getAccountsByType(AccountType accountType, Pageable pageable);

    AccountDto register(RegisterRequest registerRequest);

    AccountDto getAccountByEmail(String email);

    AccountDto verifyAccount(String token);

    LoginResponse login(LoginRequest loginRequest);

    AccountDto updateAccount(AccountDto accountDto, Principal principal);

    Account updateAccount(Account account);

    AccountDto recoverPassword(Principal principal);

    EmailDto resendVerification(EmailDto emailDto);
}

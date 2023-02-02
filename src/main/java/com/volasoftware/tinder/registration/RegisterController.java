package com.volasoftware.tinder.registration;

import com.volasoftware.tinder.accounts.Account;
import com.volasoftware.tinder.accounts.AccountService;
import com.volasoftware.tinder.constants.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegisterController {

  private final AccountService accountService;

  @PostMapping("/api/v1/users/register")
  public ResponseEntity<Account> registerNewAccount() {
    Account account = new Account();
    account.setFirstName("ivan3");
    account.setLastName("tsenov3");
    account.setEmail("ivan3.tsenov3@gmail.com");
    account.setPassword("password");
    account.setGender(Gender.MALE);
    accountService.save(account);

    return new ResponseEntity<>(account, HttpStatus.CREATED);
  }

}

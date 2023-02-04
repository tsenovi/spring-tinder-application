package com.volasoftware.tinder.registration;

import com.volasoftware.tinder.accounts.Account;
import com.volasoftware.tinder.accounts.AccountRegisterDTO;
import com.volasoftware.tinder.accounts.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegisterController {

  private final AccountService accountService;

  @PostMapping(value = "/api/v1/users/register",
      consumes = {"application/xml","application/json"})
  public ResponseEntity<Account> registerNewAccount(
      @RequestBody AccountRegisterDTO accountRegisterDTO) {

    return new ResponseEntity<>(accountService.save(accountRegisterDTO), HttpStatus.CREATED);
  }

}

package com.volasoftware.tinder.registration;

import com.volasoftware.tinder.accounts.Account;
import com.volasoftware.tinder.dtos.RegisterDTO;
import com.volasoftware.tinder.accounts.AccountService;
import com.volasoftware.tinder.responses.RegisterResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api/v1/users")
@Api(value = "Register controller")
public class RegisterController {

  private final AccountService accountService;

  @ApiOperation(value = "Register new account")
  @PostMapping(value = "/register",
      consumes = {"application/xml", "application/json"})
  public ResponseEntity<RegisterResponse> registerNewAccount(
      @ApiParam(value = "Registration details", required = true)
      @RequestBody RegisterDTO registerDTO) {

    RegisterResponse response = RegisterResponse.builder()
        .accountDTO(accountService.save(registerDTO))
        .message("Register Complete!")
        .timestamp(Instant.now())
        .build();

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

}

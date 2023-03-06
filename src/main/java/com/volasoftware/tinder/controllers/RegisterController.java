package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.dtos.RegisterDTO;
import com.volasoftware.tinder.services.contracts.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Successful operation"),
          @ApiResponse(responseCode = "400", description = "Email is already taken"),
          @ApiResponse(responseCode = "404", description = "Account not found")
      })
  @PostMapping(value = "/register",
      consumes = {"application/xml", "application/json"})
  public ResponseEntity<?> registerNewAccount(
      @ApiParam(value = "Registration details", required = true)
      @RequestBody RegisterDTO registerDTO) {

    return new ResponseEntity<>(accountService.save(registerDTO), HttpStatus.CREATED);
  }
}

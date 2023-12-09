package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.dtos.LoginRequest;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.responses.ResponseHandler;
import com.volasoftware.tinder.services.contracts.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Api(value = "Authentication controller")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @ApiOperation(value = "Register new account")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully added data!"),
                    @ApiResponse(responseCode = "400", description = "Email is already taken!"),
                    @ApiResponse(responseCode = "404", description = "Account not found!"),
                    @ApiResponse(responseCode = "406", description = "Email not valid!")
            }
    )
    @PostMapping(value = "/register",
            consumes = {"application/xml", "application/json"})
    public ResponseEntity<?> register(
            @ApiParam(value = "Registration details", required = true)
            @RequestBody RegisterRequest registerRequest) {

        return ResponseHandler.generateResponse(
                AccountConstant.REGISTERED,
                HttpStatus.OK,
                authenticationService.register(registerRequest));
    }

    @ApiOperation(value = "Login Account")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully logged in!"),
                    @ApiResponse(responseCode = "403", description = "Bad credentials!"),
                    @ApiResponse(responseCode = "400", description = "Account not verified!"),
                    @ApiResponse(responseCode = "404", description = "Account not found!")
            }
    )
    @PostMapping(value = "/login",
            consumes = {"application/xml", "application/json"})
    public ResponseEntity<?> login(
            @ApiParam(value = "Login details", required = true)
            @RequestBody LoginRequest loginRequest) {

        return ResponseHandler.generateResponse(
                AccountConstant.LOGGED_IN,
                HttpStatus.OK,
                authenticationService.login(loginRequest));
    }
}

package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.responses.ResponseHandler;
import com.volasoftware.tinder.services.contracts.AuthenticationService;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Api(value = "Authentication controller")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @ApiOperation(value = "Register new account")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully added data"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Email is already taken"),
                    @ApiResponse(
                            responseCode = "404", description = "Account not found")
            })
    @PostMapping(value = "/register",
            consumes = {"application/xml", "application/json"})
    public ResponseEntity<?> register(
            @ApiParam(value = "Registration details", required = true)
            @RequestBody RegisterRequest registerRequest) {

        return ResponseHandler.generateResponse(
                "Successfully added data!",
                HttpStatus.OK,
                authenticationService.register(registerRequest));
    }
}

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
import org.springframework.web.bind.annotation.*;

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
                            description = "Successfully added data!"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Email is already taken!"),
                    @ApiResponse(
                            responseCode = "404", description = "Account not found!"),
                    @ApiResponse(
                            responseCode = "406", description = "Email not valid!")
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

    @ApiOperation(value = "Verify account")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully verified account!"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Token expired or not exist!")
            })
    @GetMapping(value = "/verify",
            consumes = {"application/xml", "application/json"})
    public ResponseEntity<?> verify(
            @ApiParam(value = "Verify email", required = true)
            @RequestParam("token") String token) {

        return ResponseHandler.generateResponse(
                "Successfully verified account!",
                HttpStatus.OK,
                authenticationService.verify(token));
    }
}

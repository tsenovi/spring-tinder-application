package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.dtos.RegisterRequest;
import com.volasoftware.tinder.responses.ResponseHandler;
import com.volasoftware.tinder.services.contracts.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
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
                AccountConstant.REGISTERED,
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
    @GetMapping(value = "/verify")
    public ResponseEntity<?> verify(@ApiParam(value = "Verify email")
                                    @Parameter(
                                            name = "token",
                                            description = "Verification token",
                                            example = "50211ec0-b73f-49cb-a853-b694c4d5b48d")
                                    @RequestParam("token") String token) {

        return ResponseHandler.generateResponse(
                AccountConstant.VERIFIED,
                HttpStatus.OK,
                authenticationService.verify(token));
    }
}

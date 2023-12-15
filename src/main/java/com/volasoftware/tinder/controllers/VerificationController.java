package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.constants.MailConstant;
import com.volasoftware.tinder.dtos.EmailRequest;
import com.volasoftware.tinder.responses.ResponseHandler;
import com.volasoftware.tinder.services.contracts.AuthenticationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/verification")
public class VerificationController {

    private final AuthenticationService authenticationService;

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
            authenticationService.verifyAccount(token));
    }

    @ApiOperation(value = "Resend verification mail")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Successful operation!")
        }
    )
    @PostMapping(value = "/resend-verification-email")
    public ResponseEntity<?> resendVerificationMail(
        @ApiParam(value = "Account Email", required = true)
        @Valid
        @RequestBody EmailRequest emailRequest) {

        return ResponseHandler.generateResponse(
            MailConstant.SEND_VERIFICATION_MAIL,
            HttpStatus.OK,
            authenticationService.reverify(emailRequest));
    }
}

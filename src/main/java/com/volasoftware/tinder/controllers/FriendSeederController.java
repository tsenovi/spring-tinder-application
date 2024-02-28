package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.responses.ResponseHandler;
import com.volasoftware.tinder.services.contracts.FriendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("api/v1/seed-friends")
@Api(value = "Bot seeder controller")
public class FriendSeederController {

    private final FriendService friendService;

    @ApiOperation(value = "Seed friends")
    @ApiResponses(
        value = {
            @ApiResponse(code = 200, message = "Successfully added data!"),
            @ApiResponse(code = 400, message = "Invalid ID provided!"),
            @ApiResponse(code = 404, message = "Resource not found!")
        })
    @GetMapping("")
    public ResponseEntity<?> seedFriends(
        @Valid
        @Min(0L)
        @RequestParam(required = false) Long accountId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseHandler.generateResponse(
            AccountConstant.LINKED_FRIENDS,
            HttpStatus.OK,
            friendService.linkFriends(accountId, pageable));
    }
}
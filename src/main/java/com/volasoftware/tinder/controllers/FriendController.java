package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.dtos.FriendSearchDto;
import com.volasoftware.tinder.responses.ResponseHandler;
import com.volasoftware.tinder.services.contracts.FriendService;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
@Api(value = "Friends controller")
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    @ApiOperation(value = "Get sorted friends by location",
        response = FriendDto.class, responseContainer = "List")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success operation",
            response = FriendDto.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized operation"),
        @ApiResponse(code = 403, message = "Forbidden operation"),
        @ApiResponse(code = 500, message = "Failed operation")
    })
    @ApiImplicitParam(name = "friendSearchDto", value = "Friend search parameters",
        required = true, dataType = "FriendSearchDto")
    public ResponseEntity<?> getFriends(@Valid @RequestBody FriendSearchDto friendSearchDto) {

        return ResponseHandler.generateResponse(
            AccountConstant.SORTED_ACCOUNTS_BY_LOCATION,
            HttpStatus.OK,
            friendService.sortFriendsByLocation(friendSearchDto));
    }

    @ApiOperation(value = "Get friend info", response = FriendDto.class)
    @ApiResponses(
        value = {
            @ApiResponse(code = 200, message = "Success operation", response = FriendDto.class),
            @ApiResponse(code = 401, message = "Unauthorized operation"),
            @ApiResponse(code = 403, message = "Forbidden operation"),
            @ApiResponse(code = 500, message = "Failed operation")
        })
    @GetMapping("/{id}")
    public ResponseEntity<?> showFriendInfo(@PathVariable("id") Long accountId) {
        return ResponseHandler.generateResponse(
            AccountConstant.DETAILS,
            HttpStatus.OK,
            friendService.getFriendInfo(accountId));
    }
}

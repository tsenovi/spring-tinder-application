package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.dtos.LoginRequest;
import com.volasoftware.tinder.responses.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
@Api(value = "Home controller")
public class HomeController {

  @ApiOperation(value = "Home Page")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Secured home page")
      }
  )
  @GetMapping
  public ResponseEntity<?> home() {
    return ResponseHandler.generateResponse(
        "Secured home page",
        HttpStatus.OK,
        null
    );
  }
}

package com.volasoftware.tinder.responses;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {

  public static ResponseEntity<?> generateResponse(String message, HttpStatus status, Object responseObj) {
    Map<String, Object> map = new HashMap<>();
    map.put("message", message);
    map.put("status", status.value());
    map.put("data", responseObj);

    return new ResponseEntity<>(map,status);
  }
}

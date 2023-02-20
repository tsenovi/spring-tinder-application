package com.volasoftware.tinder.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response<T> {

  private HttpStatus status;
  private String message;
  private Instant timestamp;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T data;

}

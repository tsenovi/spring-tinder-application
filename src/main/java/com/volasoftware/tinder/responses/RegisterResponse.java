package com.volasoftware.tinder.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.volasoftware.tinder.dtos.AccountDTO;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {

  private String message;
  private Instant timestamp;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private AccountDTO accountDTO;

}

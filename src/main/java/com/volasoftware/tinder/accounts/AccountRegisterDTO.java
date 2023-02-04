package com.volasoftware.tinder.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRegisterDTO {

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String gender;

}

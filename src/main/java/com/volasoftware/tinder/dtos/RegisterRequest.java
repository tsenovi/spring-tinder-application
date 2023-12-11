package com.volasoftware.tinder.dtos;

import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.constraints.Password;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Register model", description = "Model for registration process")
public class RegisterRequest {

  @ApiModelProperty(value = "First name", required = true)
  @Size(min = 2, max = 50)
  @Pattern(regexp = "^[A-Za-z]*$")
  @NotBlank
  private String firstName;

  @ApiModelProperty(value = "Last name", required = true)
  @Size(min = 2, max = 50)
  @Pattern(regexp = "^[A-Za-z]*$")
  @NotBlank
  private String lastName;

  @ApiModelProperty(value = "Email address", required = true)
  @NotBlank
  @Email
  private String email;

  @ApiModelProperty(value = "Password", required = true)
  @Password
  @NotBlank
  private String password;

  @ApiModelProperty(value = "Gender", required = true)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Gender gender;
}

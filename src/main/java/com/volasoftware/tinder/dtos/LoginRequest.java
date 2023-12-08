package com.volasoftware.tinder.dtos;

import com.volasoftware.tinder.constraints.Password;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Login model", description = "Model for login process")
public class LoginRequest {

    @ApiModelProperty(value = "Email address", required = true)
    @NotBlank
    @Email
    private String email;

    @ApiModelProperty(value = "Password", required = true)
    @Password
    @NotBlank
    private String password;
}

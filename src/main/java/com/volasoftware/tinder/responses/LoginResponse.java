package com.volasoftware.tinder.responses;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Login response", description = "Response for login process")
public class LoginResponse {

    private String token;
}

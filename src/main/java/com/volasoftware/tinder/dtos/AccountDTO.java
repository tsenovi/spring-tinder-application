package com.volasoftware.tinder.dtos;


import com.volasoftware.tinder.constants.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "Public account information")
public class AccountDTO {

  @ApiModelProperty(value = "First name", example = "Ivan", required = true)
  private String firstName;

  @ApiModelProperty(value = "Last name", example = "Tsenov", required = true)
  private String lastName;

  @ApiModelProperty(value = "Email", example = "ivan.tsenov@example.com", required = true)
  private String email;

  @ApiModelProperty(value = "Gender", example = "MALE", required = true)
  @Enumerated(EnumType.STRING)
  private Gender gender;
}

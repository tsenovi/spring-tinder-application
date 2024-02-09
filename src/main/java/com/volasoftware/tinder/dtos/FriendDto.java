package com.volasoftware.tinder.dtos;

import com.volasoftware.tinder.constants.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "Friend account", description = "Public friend information")
public class FriendDto {

    @ApiModelProperty(value = "Friend first name", required = true)
    private String firstName;

    @ApiModelProperty(value = "Friend last name", required = true)
    private String lastName;

    @ApiModelProperty(value = "Friend gender", required = true)
    private Gender gender;

    @ApiModelProperty(value = "Friend age", required = true)
    private int age;

    @ApiModelProperty(value = "Friend location", required = true)
    private LocationDto locationDto;
}

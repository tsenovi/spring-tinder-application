package com.volasoftware.tinder.dtos;

import io.swagger.annotations.ApiModel;
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
@ApiModel(value = "Friend search model", description = "Model for searching friends by location")
public class FriendSearchDto {

    private Double longitude;

    private Double latitude;
}

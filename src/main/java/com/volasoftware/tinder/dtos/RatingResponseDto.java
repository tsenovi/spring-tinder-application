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
@ApiModel(value = "Rating response", description = "Response for rating friend")
public class RatingResponseDto {

    private FriendDto friendDto;

    private Integer ratingValue;

}

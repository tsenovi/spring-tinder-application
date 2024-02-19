package com.volasoftware.tinder.dtos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@ApiModel(value = "Rating friend model", description = "Model for rating friends")
public class RatingDto {

    @ApiModelProperty(value = "Friend id", required = true)
    @NotNull
    private Long friendId;

    @ApiModelProperty(value = "Rating value")
    @Min(value = 1, message = "Rating between 1 and 10")
    @Max(value = 10, message = "Rating between 1 and 10")
    private Integer rating;
}

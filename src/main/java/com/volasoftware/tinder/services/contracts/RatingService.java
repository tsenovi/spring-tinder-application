package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.dtos.RatingDto;
import com.volasoftware.tinder.dtos.RatingResponseDto;

public interface RatingService {

    //TODO
    RatingResponseDto rateFriend(RatingDto ratingDto);
}

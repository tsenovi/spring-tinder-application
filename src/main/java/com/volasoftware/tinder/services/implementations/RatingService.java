package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.dtos.RatingDto;
import com.volasoftware.tinder.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    public String rateFriend(RatingDto ratingDto) {

//TODO

        return null;
    }
}

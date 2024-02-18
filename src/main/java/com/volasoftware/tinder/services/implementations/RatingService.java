package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.RatingConstant;
import com.volasoftware.tinder.dtos.RatingDto;
import com.volasoftware.tinder.exceptions.RatingNotValidException;
import com.volasoftware.tinder.models.Rating;
import com.volasoftware.tinder.repositories.RatingRepository;
import com.volasoftware.tinder.services.contracts.FriendService;
import io.swagger.v3.core.util.ReferenceTypeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    private final FriendService friendService;

    //TODO
    public String rateFriend(RatingDto ratingDto) {
        isValidRating(ratingDto);

        Rating existingRating = ratingRepository.findByFriendId(ratingDto.getFriendId())
            .orElseGet(() -> {
                    Rating rating = new Rating();
                    rating.setAccount(loggedAccount);
                    rating.setFriend(friendAccount);
                    return rating;
                }
            );

        existingRating.setRating(ratingDto.getRating());
        ratingRepository.save(existingRating);

        return null;
    }

    private void isValidRating(RatingDto ratingDto) {
        if (ratingDto == null || ratingDto.getRating() == null) {
            throw new RatingNotValidException(RatingConstant.INVALID);
        }
    }
}

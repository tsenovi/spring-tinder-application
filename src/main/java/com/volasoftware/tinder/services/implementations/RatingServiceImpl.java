package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.RatingConstant;
import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.dtos.RatingDto;
import com.volasoftware.tinder.exceptions.RatingNotValidException;
import com.volasoftware.tinder.models.Rating;
import com.volasoftware.tinder.repositories.RatingRepository;
import com.volasoftware.tinder.dtos.RatingResponseDto;
import com.volasoftware.tinder.services.contracts.AuthenticationService;
import com.volasoftware.tinder.services.contracts.FriendService;
import com.volasoftware.tinder.services.contracts.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    private final FriendService friendService;

    private final AuthenticationService authenticationService;

    @Override
    public RatingResponseDto rateFriend(RatingDto ratingDto) {
        isValidRating(ratingDto);

        Rating existingRating = ratingRepository.findByFriendId(ratingDto.getFriendId())
            .orElseGet(() -> getNewRating(ratingDto));

        existingRating.setRating(ratingDto.getRating());
        Rating savedRating = ratingRepository.save(existingRating);

        return getRatingResponseDto(savedRating,
            friendService.getFriendInfo(ratingDto.getFriendId()));
    }

    private RatingResponseDto getRatingResponseDto(Rating rating, FriendDto friend) {
        return new RatingResponseDto(friend, rating.getRating());
    }

    private Rating getNewRating(RatingDto ratingDto) {
        Rating rating = new Rating();
        rating.setAccount(authenticationService.getLoggedAccount());
        rating.setFriend(authenticationService.getAccountById(ratingDto.getFriendId()));
        return rating;
    }

    private void isValidRating(RatingDto ratingDto) {
        if (ratingDto == null || ratingDto.getRating() == null) {
            throw new RatingNotValidException(RatingConstant.INVALID);
        }
    }
}

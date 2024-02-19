package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.constants.RatingConstant;
import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.dtos.FriendSearchDto;
import com.volasoftware.tinder.dtos.LocationDto;
import com.volasoftware.tinder.dtos.RatingDto;
import com.volasoftware.tinder.dtos.RatingResponseDto;
import com.volasoftware.tinder.exceptions.FriendNotFoundException;
import com.volasoftware.tinder.exceptions.RatingNotValidException;
import com.volasoftware.tinder.models.Rating;
import com.volasoftware.tinder.responses.ResponseHandler;
import com.volasoftware.tinder.services.contracts.FriendService;
import com.volasoftware.tinder.services.contracts.RatingService;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FriendControllerTest {

    @InjectMocks
    private FriendController friendController;

    @Mock
    private FriendService friendService;

    @Mock
    private RatingService ratingService;

    @Test
    public void testGetFriendsWhenGivenValidCoordinatesThenGettingListOfFriends() {
        //given request
        FriendSearchDto request = new FriendSearchDto();
        request.setLatitude(43.28333);
        request.setLongitude(23.6);

        //response object
        List<FriendDto> responseList = getFriendDtos();
        ResponseEntity<?> response = ResponseHandler.generateResponse(
            AccountConstant.SORTED_ACCOUNTS_BY_LOCATION,
            HttpStatus.OK,
            responseList);

        //when
        when(friendService.sortFriendsByLocation(request)).thenReturn(responseList);
        ResponseEntity<?> result = friendController.getFriends(request);

        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result);
    }

    @Test
    public void testGetFriendsWhenAccountDoesNotHaveFriendsThenReturnEmptyList() {
        //given
        FriendSearchDto request = new FriendSearchDto();
        request.setLatitude(43.28333);
        request.setLongitude(23.6);

        List<FriendDto> responseList = Collections.emptyList();
        ResponseEntity<?> response = ResponseHandler.generateResponse(
            AccountConstant.SORTED_ACCOUNTS_BY_LOCATION,
            HttpStatus.OK,
            responseList);

        //when
        when(friendService.sortFriendsByLocation(any())).thenReturn(responseList);

        //then
        ResponseEntity<?> result = friendController.getFriends(request);
        assertEquals(response, result);
    }

    @Test
    public void testGetFriendsWhenInvalidInputThenExceptionThrown() {
        // Arrange
        FriendSearchDto request = new FriendSearchDto();
        request.setLatitude(null);
        request.setLongitude(23.6);

        //when
        when(friendService.sortFriendsByLocation(request)).thenThrow(
            ConstraintViolationException.class);

        //then
        assertThrows(ConstraintViolationException.class, () -> {
            friendController.getFriends(request);
        });
    }

    @Test
    public void testShowFriendInfoWhenFriendIsFoundThenSuccessfulOperation() {
        Long accountId = 1L;
        FriendDto friendDto = new FriendDto();
        friendDto.setFirstName("Jacob");

        ResponseEntity<?> expectedResponse = ResponseHandler.generateResponse(
            AccountConstant.DETAILS,
            HttpStatus.OK,
            friendDto);

        when(friendService.getFriendInfo(accountId)).thenReturn(friendDto);

        ResponseEntity<?> actualResponse = friendController.showFriendInfo(accountId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testShowFriendInfoWhenFriendIsNotFoundThenExceptionThrown() {
        //given
        Long accountId = 1L;
        String expectedErrorMessage = "Friend not exist!";

        //when
        when(friendService.getFriendInfo(accountId)).thenThrow(
            new FriendNotFoundException(AccountConstant.FRIEND_NOT_FOUND));

        //then
        Exception exception = assertThrows(FriendNotFoundException.class,
            () -> friendController.showFriendInfo(accountId));
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }

    @Test
    public void rateGivenValidRatingDtoWhenRateFriendThenSuccessfulOperation() {
        //given
        RatingDto ratingDto = new RatingDto();
        ratingDto.setFriendId(1L);
        ratingDto.setRating(5);

        FriendDto friendDto = new FriendDto();
        friendDto.setFirstName("Jacob");

        Rating rating = new Rating();
        rating.setRating(ratingDto.getRating());

        RatingResponseDto ratingResponseDto = new RatingResponseDto(friendDto, rating.getRating());
        ResponseEntity<?> expectedResponse = ResponseHandler.generateResponse(
            RatingConstant.RATED_FRIEND,
            HttpStatus.OK,
            ratingResponseDto);

        when(ratingService.rateFriend(ratingDto)).thenReturn(ratingResponseDto);

        //when
        ResponseEntity<?> actualResponse = friendController.rateFriend(ratingDto);

        //then
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGivenInvalidRatingDtoWhenRateFriendThenFailedOperation() {
        //given
        RatingDto ratingDto = new RatingDto();
        ratingDto.setFriendId(1L);
        ratingDto.setRating(11); // Invalid rating
        String expectedErrorMessage = RatingConstant.INVALID;

        when(ratingService.rateFriend(ratingDto)).thenThrow(
            new RatingNotValidException(RatingConstant.INVALID));

        //when
        RatingNotValidException actualException = assertThrows(
            RatingNotValidException.class, () -> friendController.rateFriend(ratingDto));

        //then
        assertTrue(actualException.getMessage().contains(expectedErrorMessage));
    }

    private List<FriendDto> getFriendDtos() {
        LocationDto locationDto1 = new LocationDto(43.5669131509647, 23.6549237093558);
        LocationDto locationDto2 = new LocationDto(43.95242677801008, 23.708561103846055);
        LocationDto locationDto3 = new LocationDto(43.960959300829515, 23.847866029518798);

        FriendDto friendDto1 = new FriendDto("Jacob", "Jacques", Gender.FEMALE, 26, locationDto1);
        FriendDto friendDto2 = new FriendDto("Jacob", "Jacaruso", Gender.MALE, 19, locationDto2);
        FriendDto friendDto3 = new FriendDto("James", "Jablon", Gender.OTHER, 81, locationDto3);

        List<FriendDto> friendDtos = new ArrayList<>();
        friendDtos.add(friendDto1);
        friendDtos.add(friendDto2);
        friendDtos.add(friendDto3);

        return friendDtos;
    }
}
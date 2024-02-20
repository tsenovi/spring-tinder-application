package com.volasoftware.tinder.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.constants.RatingConstant;
import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.dtos.FriendSearchDto;
import com.volasoftware.tinder.dtos.LocationDto;
import com.volasoftware.tinder.dtos.RatingDto;
import com.volasoftware.tinder.exceptions.FriendNotFoundException;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.Rating;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.responses.ResponseHandler;
import com.volasoftware.tinder.services.contracts.FriendService;
import com.volasoftware.tinder.services.contracts.RatingService;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FriendControllerTest {

    @Autowired
    private FriendController friendController;

    @Autowired
    private FriendService friendService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    private Authentication authentication;

    private MockMvc mockMvc;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        authentication = new UsernamePasswordAuthenticationToken("user", "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

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
    public void rateGivenValidRatingDtoWhenRateFriendThenSuccessfulOperation() throws Exception {
        //given
        RatingDto ratingDto = new RatingDto();
        ratingDto.setFriendId(1L);
        ratingDto.setRating(5);

        Account friendAccount = new Account();
        friendAccount.setFirstName("Jacob");
        friendAccount.setId(1L);
        accountRepository.save(friendAccount);

        Account account = new Account();
        account.setId(2L);
        account.setEmail("user");
        account.setPassword(passwordEncoder.encode("password"));
        account.setFriends(new HashSet<>());
        account.getFriends().add(friendAccount);
        accountRepository.save(account);

        FriendDto friendDto = new FriendDto();
        friendDto.setFirstName("Jacob");

        Rating rating = new Rating();
        rating.setRating(ratingDto.getRating());

        //then
        mockMvc.perform(post("/api/v1/friends/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(ratingDto)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(RatingConstant.RATED_FRIEND)));
    }

    @Test
    public void testGivenInvalidRatingDtoWhenRateFriendThenFailedOperation() {
        //given
        RatingDto ratingDto = new RatingDto();
        ratingDto.setFriendId(1L);
        ratingDto.setRating(11);

        //when
        Exception exception = assertThrows(ConstraintViolationException.class,
            () -> friendController.rateFriend(ratingDto));

        //then
        String expectedMessage = "Rating between 1 and 10";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
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
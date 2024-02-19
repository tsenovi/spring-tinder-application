package com.volasoftware.tinder.services.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.constants.RatingConstant;
import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.dtos.RatingDto;
import com.volasoftware.tinder.dtos.RatingResponseDto;
import com.volasoftware.tinder.exceptions.RatingNotValidException;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.Rating;
import com.volasoftware.tinder.repositories.RatingRepository;
import com.volasoftware.tinder.services.contracts.AuthenticationService;
import com.volasoftware.tinder.services.contracts.FriendService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class RatingServiceImplTest {

    @MockBean
    private RatingRepository ratingRepository;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private FriendService friendService;

    private RatingServiceImpl ratingService;

    @BeforeEach
    public void setUp() {
        ratingService = new RatingServiceImpl(ratingRepository, friendService,
            authenticationService);
    }

    @Test
    public void testGivenValidRatingWhenRateFriendThenReturnRatingResponseDto() {
        //given
        RatingDto ratingDto = new RatingDto();
        ratingDto.setFriendId(2L);
        ratingDto.setRating(5);

        Account loggedAccount = new Account();
        loggedAccount.setId(1L);
        Account friend = new Account();
        friend.setId(2L);

        FriendDto friendDto = new FriendDto();
        friendDto.setFirstName("Jacob");

        Rating rating = new Rating();
        rating.setAccount(loggedAccount);
        rating.setFriend(friend);

        when(ratingRepository.findByFriendId(ratingDto.getFriendId())).thenReturn(
            Optional.of(rating));
        when(authenticationService.getLoggedAccount()).thenReturn(loggedAccount);
        when(authenticationService.getAccountById(anyLong())).thenReturn(friend);
        rating.setRating(ratingDto.getRating());
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(friendService.getFriendInfo(ratingDto.getFriendId())).thenReturn(friendDto);

        //when
        RatingResponseDto result = ratingService.rateFriend(ratingDto);

        //then
        assertNotNull(result);
        assertEquals("Jacob", result.getFriendDto().getFirstName());
        assertEquals(5, result.getRatingValue());
    }

    @Test
    public void testGivenInvalidRatingWhenRateFriendThenExceptionThrown() {
        //given
        RatingDto ratingDto = new RatingDto();
        ratingDto.setFriendId(2L);
        ratingDto.setRating(null);
        String expectedErrorMessage = RatingConstant.INVALID;

        //when
        RatingNotValidException exception = assertThrows(
            RatingNotValidException.class, () -> ratingService.rateFriend(ratingDto));

        //then
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }
}
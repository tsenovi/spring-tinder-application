package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.dtos.FriendSearchDto;
import com.volasoftware.tinder.dtos.LocationDto;
import com.volasoftware.tinder.responses.ResponseHandler;
import com.volasoftware.tinder.services.contracts.FriendService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FriendControllerTest {

    @InjectMocks
    private FriendController friendController;

    @Mock
    private FriendService friendService;

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
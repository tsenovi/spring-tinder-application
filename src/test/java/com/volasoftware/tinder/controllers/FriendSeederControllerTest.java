package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.constants.OperationConstant;
import com.volasoftware.tinder.services.contracts.FriendService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FriendSeederControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendService friendService;

    @Test
    public void testSeedFriendsWhenValidIdIsProvidedThenReturnSuccess() throws Exception {
        //when
        Long accountId = 1L;
        when(friendService.linkRequestedAccountWithBots(accountId, Pageable.unpaged()))
            .thenReturn(OperationConstant.SUCCESSFUL);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seed-friends")
                .param("id", accountId.toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSeedFriendsWhenNoIdProvidedThenReturnSuccess() throws Exception {
        //when
        when(friendService.linkAllAccountsWithBots(Pageable.unpaged()))
            .thenReturn(OperationConstant.SUCCESSFUL);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/seed-friends")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
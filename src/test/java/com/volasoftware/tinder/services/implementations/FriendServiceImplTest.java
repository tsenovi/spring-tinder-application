package com.volasoftware.tinder.services.implementations;

import static org.junit.jupiter.api.Assertions.*;

import com.volasoftware.tinder.models.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class FriendServiceImplTest {

    @MockBean
    private FriendServiceImpl friendService;

    @Test
    void testAddFriendWhenBothAccountsExistThenSuccessfulOperation() {
        //given
        Account loggedAccount = new Account();
        loggedAccount.setId(1L);
        loggedAccount.setEmail("john@gmail.com");
        Account friendAccount = new Account();
        friendAccount.setId(2L);
        friendAccount.setEmail("jane@gmail.com");


    }

    @Test
    void removeFriend() {
    }
}
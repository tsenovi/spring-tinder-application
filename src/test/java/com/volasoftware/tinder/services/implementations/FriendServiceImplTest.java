package com.volasoftware.tinder.services.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class FriendServiceImplTest {

    public static final String USERNAME = "loggedUser";

    public static final long FRIEND_ID = 2L;

    public static final long LOGGED_ACCOUNT_ID = 1L;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private Authentication authentication;

    @InjectMocks
    private FriendServiceImpl friendService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        friendService = new FriendServiceImpl(accountRepository);
    }

    @Test
    void testAddFriendWhenFriendExistThenLoggedAccountContainsThatFriend() {
        Account loggedAccount = new Account();
        loggedAccount.setId(LOGGED_ACCOUNT_ID);
        loggedAccount.setFriends(new HashSet<>());

        Account friendAccount = new Account();
        friendAccount.setId(FRIEND_ID);

        when(authentication.getName()).thenReturn(USERNAME);
        when(accountRepository.findOneByEmail(USERNAME)).thenReturn(Optional.of(loggedAccount));
        when(accountRepository.findById(FRIEND_ID)).thenReturn(
            Optional.of(friendAccount));

        friendService.addFriend(friendAccount.getId());

        verify(accountRepository, times(1)).save(loggedAccount);
        assert (loggedAccount.getFriends().contains(friendAccount));
    }

    @Test
    void testRemoveFriendWhenFriendExistThenLoggedAccountDoesNotContainsThatFriend() {
        Account loggedAccount = new Account();
        loggedAccount.setId(LOGGED_ACCOUNT_ID);
        loggedAccount.setFriends(new HashSet<>());

        Account friendAccount = new Account();
        friendAccount.setId(FRIEND_ID);
        loggedAccount.getFriends().add(friendAccount);

        when(authentication.getName()).thenReturn(USERNAME);
        when(accountRepository.findOneByEmail(USERNAME)).thenReturn(Optional.of(loggedAccount));
        when(accountRepository.findById(FRIEND_ID)).thenReturn(
            Optional.of(friendAccount));

        friendService.removeFriend(friendAccount.getId());

        verify(accountRepository, times(1)).save(loggedAccount);
        assert (!loggedAccount.getFriends().contains(friendAccount));
    }

    @Test
    void testAddFriendWhenLoggedAccountNotFoundThenThrowAccountNotFoundException() {
        when(authentication.getName()).thenReturn(USERNAME);
        when(accountRepository.findOneByEmail(USERNAME)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> friendService.addFriend(FRIEND_ID));
    }

    @Test
    void testAddFriendWhenFriendAccountNotFoundThenThrowAccountNotFoundException() {
        Account loggedAccount = new Account();

        when(authentication.getName()).thenReturn(USERNAME);
        when(accountRepository.findOneByEmail(USERNAME)).thenReturn(Optional.of(loggedAccount));
        when(accountRepository.findById(FRIEND_ID)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> friendService.addFriend(FRIEND_ID));
    }

    @Test
    void testRemoveFriendWhenLoggedAccountNotFoundThenThrowAccountNotFoundException() {
        when(authentication.getName()).thenReturn(USERNAME);
        when(accountRepository.findOneByEmail(USERNAME)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> friendService.removeFriend(FRIEND_ID));
    }

    @Test
    void testRemoveFriendWhenFriendAccountNotFoundThenThrowAccountNotFoundException() {
        Account loggedAccount = new Account();

        when(authentication.getName()).thenReturn(USERNAME);
        when(accountRepository.findOneByEmail(USERNAME)).thenReturn(Optional.of(loggedAccount));
        when(accountRepository.findById(FRIEND_ID)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> friendService.removeFriend(FRIEND_ID));
    }
}
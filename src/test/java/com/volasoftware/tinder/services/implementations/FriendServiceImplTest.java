package com.volasoftware.tinder.services.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.constants.AccountType;
import com.volasoftware.tinder.constants.OperationConstant;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.FriendExistException;
import com.volasoftware.tinder.exceptions.NoRealAccountsException;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.utils.BotInitializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class FriendServiceImplTest {

    public static final String USERNAME = "loggedUser";

    public static final long FRIEND_ID = 2L;

    public static final long LOGGED_ACCOUNT_ID = 1L;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private Random random;

    @MockBean
    private Authentication authentication;

    @MockBean
    private BotInitializer botInitializer;

    @InjectMocks
    private FriendServiceImpl friendService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        friendService = new FriendServiceImpl(accountRepository, random);
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

    @Test
    public void testLinkRequestedAccountWithBotsWhenAccountHasNoFriendsThenSuccessOperation() {
        //given
        Account realAccount = createAccount(1L, AccountType.REAL);
        Account botAccount1 = createAccount(2L, AccountType.BOT);
        Account botAccount2 = createAccount(3L, AccountType.BOT);

        Set<Account> botSet = new HashSet<>();
        botSet.add(botAccount1);
        botSet.add(botAccount2);

        Account savedAccount = createAccount(1L, AccountType.REAL);
        savedAccount.setFriends(botSet);
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        //when
        when(accountRepository.findById(realAccount.getId())).thenReturn(Optional.of(realAccount));
        when(accountRepository.findByAccountType(AccountType.BOT, pageable)).thenReturn(
            new PageImpl<>(new ArrayList<>(botSet)));
        when(accountRepository.save(realAccount)).thenReturn(savedAccount);

        // Act
        String result = friendService.linkRequestedAccountWithBots(realAccount.getId(), pageable);

        //then
        assertEquals(OperationConstant.SUCCESSFUL, result);
        assertTrue(savedAccount.getFriends().contains(botAccount1));
        assertTrue(savedAccount.getFriends().contains(botAccount2));
    }

    @Test
    public void testLinkRequestedAccountWhenBotsAreAlreadyFriendsThenExceptionIsThrown() {
        //given
        Account realAccount = createAccount(1L, AccountType.REAL);
        Account botAccount1 = createAccount(2L, AccountType.BOT);
        Account botAccount2 = createAccount(3L, AccountType.BOT);

        Set<Account> botSet = new HashSet<>();
        botSet.add(botAccount1);
        botSet.add(botAccount2);

        realAccount.setFriends(botSet);
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        //when
        when(accountRepository.findById(realAccount.getId())).thenReturn(Optional.of(realAccount));
        when(accountRepository.findByAccountType(AccountType.BOT, pageable)).thenReturn(
            new PageImpl<>(new ArrayList<>(botSet)));
        when(accountRepository.save(realAccount)).thenReturn(realAccount);

        //then
        assertThrows(FriendExistException.class,
            () -> friendService.linkRequestedAccountWithBots(realAccount.getId(), pageable));
    }

    @Test
    public void testLinkAllAccountsWithBotsWhenNoRealAccountsExistThenExceptionIsThrown() {
        //given
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        //when
        when(accountRepository.findByAccountType(AccountType.REAL, pageable)).thenReturn(
            new PageImpl<>(new ArrayList<>()));

        //then
        assertThrows(NoRealAccountsException.class,
            () -> friendService.linkAllAccountsWithBots(pageable));
    }

    @Test
    public void testLinkAllAccountsWithBotsWhenRealAccountsDoesNotHaveBotsThenSuccessOperation() {
        //given
        Account realAccount1 = createAccount(1L, AccountType.REAL);
        Account realAccount2 = createAccount(2L, AccountType.REAL);
        Account botAccount1 = createAccount(3L, AccountType.BOT);
        Account botAccount2 = createAccount(4L, AccountType.BOT);

        Set<Account> realAccountSet = new HashSet<>();
        realAccountSet.add(realAccount1);
        realAccountSet.add(realAccount2);

        Set<Account> botSet = new HashSet<>();
        botSet.add(botAccount1);
        botSet.add(botAccount2);

        Account savedAccount1 = createAccount(1L, AccountType.REAL);
        savedAccount1.setFriends(botSet);
        Account savedAccount2 = createAccount(2L, AccountType.REAL);
        savedAccount2.setFriends(botSet);

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        //when
        when(accountRepository.findByAccountType(AccountType.REAL, pageable)).thenReturn(
            new PageImpl<>(new ArrayList<>(realAccountSet)));
        when(accountRepository.findByAccountType(AccountType.BOT, pageable)).thenReturn(
            new PageImpl<>(new ArrayList<>(botSet)));
        when(accountRepository.save(realAccount1)).thenReturn(savedAccount1);
        when(accountRepository.save(realAccount2)).thenReturn(savedAccount2);

        //then
        String result = friendService.linkAllAccountsWithBots(pageable);
        assertEquals(OperationConstant.SUCCESSFUL, result);
    }

    private Account createAccount(Long id, AccountType type) {
        Account account = new Account();
        account.setId(id);
        account.setAccountType(type);
        return account;
    }
}
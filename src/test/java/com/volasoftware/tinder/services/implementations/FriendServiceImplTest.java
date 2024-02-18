package com.volasoftware.tinder.services.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.constants.AccountType;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.constants.OperationConstant;
import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.dtos.FriendSearchDto;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.FriendExistException;
import com.volasoftware.tinder.exceptions.NoRealAccountsException;
import com.volasoftware.tinder.mapper.FriendMapper;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.Location;
import com.volasoftware.tinder.services.contracts.AuthenticationService;
import com.volasoftware.tinder.utils.BotInitializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.boot.test.mock.mockito.MockBean;
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
    private AuthenticationService authenticationService;

    @MockBean
    private Random random;

    @MockBean
    private Authentication authentication;

    @MockBean
    private BotInitializer botInitializer;

    @MockBean
    private FriendMapper friendMapper;

    @InjectMocks
    private FriendServiceImpl friendService;

    private Account loggedAccount;
    private Account friend1;
    private Account friend2;
    private Account friend3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        friendService = new FriendServiceImpl(authenticationService, random, friendMapper);
        createAccountWithFriends();
    }

    @Test
    void testAddFriendWhenFriendExistThenLoggedAccountContainsThatFriend() {
        Account loggedAccount = new Account();
        loggedAccount.setId(LOGGED_ACCOUNT_ID);
        loggedAccount.setFriends(new HashSet<>());

        Account friendAccount = new Account();
        friendAccount.setId(FRIEND_ID);

        when(authentication.getName()).thenReturn(USERNAME);
        when(authenticationService.getLoggedAccount()).thenReturn(loggedAccount);
        when(authenticationService.getAccountById(FRIEND_ID)).thenReturn(friendAccount);

        friendService.addFriend(friendAccount.getId());

        verify(authenticationService, times(1)).updateAccount(loggedAccount);
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
        when(authenticationService.getLoggedAccount()).thenReturn(loggedAccount);
        when(authenticationService.getAccountById(FRIEND_ID)).thenReturn(friendAccount);

        friendService.removeFriend(friendAccount.getId());

        verify(authenticationService, times(1)).updateAccount(loggedAccount);
        assert (!loggedAccount.getFriends().contains(friendAccount));
    }

    @Test
    void testAddFriendWhenLoggedAccountNotFoundThenThrowAccountNotFoundException() {
        when(authentication.getName()).thenReturn(USERNAME);
        when(authenticationService.getLoggedAccount()).thenThrow(AccountNotFoundException.class);

        assertThrows(AccountNotFoundException.class, () -> friendService.addFriend(FRIEND_ID));
    }

    @Test
    void testAddFriendWhenFriendAccountNotFoundThenThrowAccountNotFoundException() {
        Account loggedAccount = new Account();

        when(authentication.getName()).thenReturn(USERNAME);
        when(authenticationService.getLoggedAccount()).thenReturn(loggedAccount);
        when(authenticationService.getAccountById(FRIEND_ID)).thenThrow(
            AccountNotFoundException.class);

        assertThrows(AccountNotFoundException.class, () -> friendService.addFriend(FRIEND_ID));
    }

    @Test
    void testRemoveFriendWhenLoggedAccountNotFoundThenThrowAccountNotFoundException() {
        when(authentication.getName()).thenReturn(USERNAME);
        when(authenticationService.getLoggedAccount()).thenThrow(AccountNotFoundException.class);

        assertThrows(AccountNotFoundException.class, () -> friendService.removeFriend(FRIEND_ID));
    }

    @Test
    void testRemoveFriendWhenFriendAccountNotFoundThenThrowAccountNotFoundException() {
        Account loggedAccount = new Account();

        when(authentication.getName()).thenReturn(USERNAME);
        when(authenticationService.getLoggedAccount()).thenReturn(loggedAccount);
        when(authenticationService.getAccountById(FRIEND_ID)).thenThrow(
            AccountNotFoundException.class);

        assertThrows(AccountNotFoundException.class, () -> friendService.removeFriend(FRIEND_ID));
    }

    @Test
    public void testLinkRequestedAccountWithBotsWhenAccountHasNoFriendsThenSuccessOperation() {
        //given
        Account realAccount = createAccountByType(1L, AccountType.REAL);
        Account botAccount1 = createAccountByType(2L, AccountType.BOT);
        Account botAccount2 = createAccountByType(3L, AccountType.BOT);

        Set<Account> botSet = new HashSet<>();
        botSet.add(botAccount1);
        botSet.add(botAccount2);

        Account savedAccount = createAccountByType(1L, AccountType.REAL);
        savedAccount.setFriends(botSet);
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        //when
        when(authenticationService.getAccountById(realAccount.getId())).thenReturn(realAccount);
        when(authenticationService.getAccountsByType(AccountType.BOT, pageable)).thenReturn(botSet);
        when(authenticationService.updateAccount(realAccount)).thenReturn(savedAccount);

        // Act
        String result = friendService.linkFriends(realAccount.getId(), pageable);

        //then
        assertEquals(OperationConstant.SUCCESSFUL, result);
        assertTrue(savedAccount.getFriends().contains(botAccount1));
        assertTrue(savedAccount.getFriends().contains(botAccount2));
    }

    @Test
    public void testLinkRequestedAccountWhenBotsAreAlreadyFriendsThenExceptionIsThrown() {
        //given
        Account realAccount = createAccountByType(1L, AccountType.REAL);
        Account botAccount1 = createAccountByType(2L, AccountType.BOT);
        Account botAccount2 = createAccountByType(3L, AccountType.BOT);

        Set<Account> botSet = new HashSet<>();
        botSet.add(botAccount1);
        botSet.add(botAccount2);

        realAccount.setFriends(botSet);
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        //when
        when(authenticationService.getAccountById(realAccount.getId())).thenReturn(realAccount);
        when(authenticationService.getAccountsByType(AccountType.BOT, pageable)).thenReturn(botSet);
        when(authenticationService.updateAccount(realAccount)).thenReturn(realAccount);

        //then
        assertThrows(FriendExistException.class,
            () -> friendService.linkFriends(realAccount.getId(), pageable));
    }

    @Test
    public void testLinkAllAccountsWithBotsWhenNoRealAccountsExistThenExceptionIsThrown() {
        //given
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        //when
        when(authenticationService.getAccountsByType(AccountType.REAL, pageable)).thenThrow(
            NoRealAccountsException.class);

        //then
        assertThrows(NoRealAccountsException.class,
            () -> friendService.linkFriends(null, pageable));
    }

    @Test
    public void testLinkAllAccountsWithBotsWhenRealAccountsDoesNotHaveBotsThenSuccessOperation() {
        //given
        Account realAccount1 = createAccountByType(1L, AccountType.REAL);
        Account realAccount2 = createAccountByType(2L, AccountType.REAL);
        Account botAccount1 = createAccountByType(3L, AccountType.BOT);
        Account botAccount2 = createAccountByType(4L, AccountType.BOT);

        Set<Account> realAccountSet = new HashSet<>();
        realAccountSet.add(realAccount1);
        realAccountSet.add(realAccount2);

        Set<Account> botSet = new HashSet<>();
        botSet.add(botAccount1);
        botSet.add(botAccount2);

        Account savedAccount1 = createAccountByType(1L, AccountType.REAL);
        savedAccount1.setFriends(botSet);
        Account savedAccount2 = createAccountByType(2L, AccountType.REAL);
        savedAccount2.setFriends(botSet);

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        //when
        when(authenticationService.getAccountsByType(AccountType.REAL, pageable)).thenReturn(
            realAccountSet);
        when(authenticationService.getAccountsByType(AccountType.BOT, pageable)).thenReturn(botSet);
        when(authenticationService.updateAccount(realAccount1)).thenReturn(savedAccount1);
        when(authenticationService.updateAccount(realAccount2)).thenReturn(savedAccount2);

        //then
        String result = friendService.linkFriends(null, pageable);
        assertEquals(OperationConstant.SUCCESSFUL, result);
    }


    @Test
    public void testSortFriendsByLocation() {
        //given
        FriendSearchDto friendSearchDto = new FriendSearchDto();
        friendSearchDto.setLatitude(0.0);
        friendSearchDto.setLongitude(0.0);

        //when
        List<Account> friendsList = Arrays.asList(friend1, friend2, friend3);
        ArrayList<Account> friends = new ArrayList<>(friendsList);
        when(authenticationService.getLoggedAccount()).thenReturn(loggedAccount);
        when(authenticationService.getFriendsByLocation(loggedAccount,
            friendSearchDto.getLatitude(), friendSearchDto.getLongitude())).thenReturn(friends);

        List<FriendDto> expectedFriendDtos = friendMapper.accountListToFriendDtoList(friends);
        List<FriendDto> sortedFriendDtos = friendService.sortFriendsByLocation(friendSearchDto);

        //then
        assertEquals(expectedFriendDtos, sortedFriendDtos);
    }

    @Test
    void testGetFriendInfoWhenFriendExistThenReturnFriendDto() {
        //given
        String email = "jacob@gmail.com";
        Long friendId = 1L;

        Account loggedAccount = new Account();
        loggedAccount.setEmail(email);
        loggedAccount.setFriends(new HashSet<>());

        Account friend = new Account();
        friend.setId(friendId);
        loggedAccount.getFriends().add(friend);

        FriendDto friendDto = new FriendDto();
        friendDto.setFirstName("Jacob");

        //when
        when(authentication.getName()).thenReturn(USERNAME);
        when(authenticationService.getLoggedAccount()).thenReturn(loggedAccount);
        when(authenticationService.getAccountById(friendId)).thenReturn(friend);
        when(friendMapper.accountToFriendDto(friend)).thenReturn(friendDto);

        FriendDto result = friendService.getFriendInfo(friendId);

        //then
        assertEquals(friendDto.getFirstName(), result.getFirstName());
    }

    private Account createAccountByType(Long id, AccountType type) {
        Account account = new Account();
        account.setId(id);
        account.setAccountType(type);
        return account;
    }

    private void createAccountWithFriends() {
        loggedAccount = new Account();
        loggedAccount.setId(1L);
        loggedAccount.setEmail("user1");

        friend1 = createAccountWithLocation(2L, "user2", "John", "Doe",
            Gender.MALE, 21, 10.0, 10.0);
        friend2 = createAccountWithLocation(3L, "user3", "Jane", "Doe",
            Gender.FEMALE, 22, 20.0, 20.0);
        friend3 = createAccountWithLocation(4L, "user4", "Alice", "Doe",
            Gender.FEMALE, 30, 30.0, 30.0);

        loggedAccount.setFriends(Set.of(friend1, friend2, friend3));
    }

    private Account createAccountWithLocation(Long id, String email, String firstName,
        String lastName, Gender gender, int age, double latitude, double longitude) {
        Account account = new Account();
        account.setId(id);
        account.setEmail(email);
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setGender(gender);
        account.setAge(age);
        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        account.setLocation(location);
        return account;
    }
}

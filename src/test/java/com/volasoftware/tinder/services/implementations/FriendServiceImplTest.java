package com.volasoftware.tinder.services.implementations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.constants.AccountType;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.constants.OperationConstant;
import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.dtos.FriendSearchDto;
import com.volasoftware.tinder.dtos.LocationDto;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.FriendExistException;
import com.volasoftware.tinder.exceptions.NoRealAccountsException;
import com.volasoftware.tinder.mapper.FriendMapper;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.Location;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.utils.BotInitializer;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.mockito.Mock;
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
        friendService = new FriendServiceImpl(accountRepository, random, friendMapper);
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


    @Test
    public void testSortFriendsByLocation() {
        //given
        FriendSearchDto friendSearchDto = new FriendSearchDto();
        friendSearchDto.setLatitude(0.0);
        friendSearchDto.setLongitude(0.0);

        List<FriendDto> expectedFriends = Arrays.asList(
            FriendDto.builder().firstName("John").lastName("Doe")
                .gender(Gender.MALE).age(25)
                .locationDto(LocationDto.builder().latitude(10.0).longitude(10.0).build()).build(),
            FriendDto.builder().firstName("Jane").lastName("Doe")
                .gender(Gender.FEMALE).age(22)
                .locationDto(LocationDto.builder().latitude(20.0).longitude(20.0).build()).build(),
            FriendDto.builder().firstName("Alice").lastName("Doe")
                .gender(Gender.OTHER).age(30)
                .locationDto(LocationDto.builder().latitude(30.0).longitude(30.0).build()).build()
        );

        //when
        List<Account> friendsList = Arrays.asList(friend1, friend2, friend3);
        ArrayList<Account> friends = new ArrayList<>(friendsList);
        when(accountRepository.findOneByEmail(any())).thenReturn(
            Optional.ofNullable(loggedAccount));
        when(friendMapper.accountListToFriendDtoList(friends)).thenReturn(expectedFriends);

        List<FriendDto> expectedFriendDtos = friendMapper.accountListToFriendDtoList(friends);
        List<FriendDto> sortedFriendDtos = friendService.sortFriendsByLocation(friendSearchDto);

        //then
        assertEquals(expectedFriendDtos, sortedFriendDtos);
    }

    private Account createAccount(Long id, AccountType type) {
        Account account = new Account();
        account.setId(id);
        account.setAccountType(type);
        return account;
    }

    private void createAccountWithFriends() {
        loggedAccount = new Account();
        loggedAccount.setId(1L);
        loggedAccount.setEmail("user1");

        friend1 = new Account();
        friend1.setId(2L);
        friend1.setEmail("user2");
        friend1.setFirstName("John");
        friend1.setLastName("Doe");
        friend1.setGender(Gender.MALE);
        friend1.setAge(25);
        Location location1 = new Location();
        location1.setLatitude(10.0);
        location1.setLongitude(10.0);
        friend1.setLocation(location1);

        friend2 = new Account();
        friend2.setId(3L);
        friend2.setEmail("user3");
        friend2.setFirstName("Jane");
        friend2.setLastName("Doe");
        friend2.setGender(Gender.FEMALE);
        friend2.setAge(22);
        Location location2 = new Location();
        location2.setLatitude(20.0);
        location2.setLongitude(20.0);
        friend2.setLocation(location2);

        friend3 = new Account();
        friend3.setId(4L);
        friend3.setEmail("user4");
        friend3.setFirstName("Alice");
        friend3.setLastName("Doe");
        friend3.setGender(Gender.OTHER);
        friend3.setAge(30);
        Location location3 = new Location();
        location3.setLatitude(30.0);
        location3.setLongitude(30.0);
        friend3.setLocation(location3);

        loggedAccount.setFriends(Set.of(friend1, friend2, friend3));
    }
}

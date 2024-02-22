package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.constants.AccountType;
import com.volasoftware.tinder.constants.OperationConstant;
import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.dtos.FriendSearchDto;
import com.volasoftware.tinder.exceptions.FriendExistException;
import com.volasoftware.tinder.exceptions.FriendNotFoundException;
import com.volasoftware.tinder.exceptions.NoRealAccountsException;
import com.volasoftware.tinder.mapper.FriendMapper;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.services.contracts.AccountService;
import com.volasoftware.tinder.services.contracts.FriendService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 10;
    public static final int THREAD_DELAY = 10000;

    private final AccountService accountService;

    private final Random random;

    private final FriendMapper friendMapper;

    @Override
    public void addFriend(Long friendId) {
        Account loggedAccount = accountService.getLoggedAccount();
        Account friendAccount = accountService.getAccountById(friendId);

        if (hasFriends(loggedAccount)) {
            if (friendExist(loggedAccount, friendAccount)) {
                throw new FriendExistException(AccountConstant.ALREADY_FRIEND);
            }
        } else {
            loggedAccount.setFriends(new HashSet<>());
        }

        loggedAccount.getFriends().add(friendAccount);
        accountService.updateAccount(loggedAccount);
    }

    @Override
    public void removeFriend(Long friendId) {
        Account loggedAccount = accountService.getLoggedAccount();
        Account friendAccount = accountService.getAccountById(friendId);

        if (friendExist(loggedAccount, friendAccount)) {
            loggedAccount.getFriends().remove(friendAccount);
            accountService.updateAccount(loggedAccount);
        } else {
            throw new FriendNotFoundException(AccountConstant.FRIEND_NOT_FOUND);
        }
    }

    @Override
    public List<FriendDto> sortFriendsByLocation(FriendSearchDto friendSearchDto) {
        Double accountLatitude = friendSearchDto.getLatitude();
        Double accountLongitude = friendSearchDto.getLongitude();
        Account loggedAccount = accountService.getLoggedAccount();

        List<Account> sortedFriends = accountService.getFriendsByLocation(loggedAccount,
            accountLatitude, accountLongitude);
        if (sortedFriends.isEmpty()) {
            return Collections.emptyList();
        }

        return friendMapper.accountListToFriendDtoList(sortedFriends);
    }

    @Override
    public String linkFriends(Long accountId, Pageable pageable) {
        if (accountId != null) {
            return linkRequestedAccountWithBots(accountId, pageable);
        }

        return linkAllAccountsWithBots(pageable);
    }

    @Override
    public FriendDto getFriendInfo(Long accountId) {
        Account loggedAccount = accountService.getLoggedAccount();
        Account friendAccount = accountService.getAccountById(accountId);
        if (friendExist(loggedAccount, friendAccount)) {
            return friendMapper.accountToFriendDto(friendAccount);
        }

        throw new FriendNotFoundException(AccountConstant.FRIEND_NOT_FOUND);
    }

    @Async
    @Override
    public void executeAsyncLinkFriends(Long accountId) {
        try {
            Thread.sleep(THREAD_DELAY);
            Pageable pageable = createPageRequest();
            linkRequestedAccountWithBots(accountId, pageable);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Pageable createPageRequest() {
        return PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    }


    private String linkRequestedAccountWithBots(Long accountId, Pageable pageable) {
        Account realAccount = accountService.getAccountById(accountId);

        Set<Account> botSet = accountService.getAccountsByType(AccountType.BOT,
            pageable);

        int initialFriendsCount = getFriendsCount(realAccount);
        if (!hasFriends(realAccount)) {
            realAccount.setFriends(new HashSet<>());
        }

        Account savedAccount = addBotFriendsToExistingSet(realAccount, botSet);

        int finalFriendsCount = getFriendsCount(savedAccount);
        if (finalFriendsCount > initialFriendsCount) {
            return OperationConstant.SUCCESSFUL;
        }

        throw new FriendExistException(OperationConstant.FAILED);
    }


    private String linkAllAccountsWithBots(Pageable pageable) {
        Set<Account> realAccounts = accountService.getAccountsByType(AccountType.REAL,
            pageable);
        if (realAccounts.isEmpty()) {
            throw new NoRealAccountsException(AccountConstant.ACCOUNTS_NOT_EXIST);
        }

        Set<Account> botSet = accountService.getAccountsByType(AccountType.BOT,
            pageable);

        int initialFriendsCount = getFriendsCount(realAccounts);
        int finalFriendsCount = PAGE_NUMBER;
        for (Account realAccount : realAccounts) {
            if (!hasFriends(realAccount)) {
                realAccount.setFriends(new HashSet<>());
            }

            Account savedAccount = addBotFriendsToExistingSet(realAccount, botSet);
            finalFriendsCount += getFriendsCount(savedAccount);
        }

        if (finalFriendsCount > initialFriendsCount) {
            return OperationConstant.SUCCESSFUL;
        }

        throw new FriendExistException(OperationConstant.FAILED);
    }

    private int getFriendsCount(Account account) {
        return account.getFriends() == null ? PAGE_NUMBER : account.getFriends().size();
    }

    private int getFriendsCount(Set<Account> realAccounts) {
        return realAccounts.stream().mapToInt(this::getFriendsCount).sum();
    }

    private Account addBotFriendsToExistingSet(Account account, Set<Account> botAccounts) {
        Set<Account> botFriends = account.getFriends();
        int randomBotsCount = random.nextInt(botAccounts.size());
        List<Account> botAccountList = new ArrayList<>(botAccounts);
        botAccountList.removeIf(botFriends::contains);
        botFriends.addAll(botAccountList.subList(PAGE_NUMBER, randomBotsCount));
        account.setFriends(botFriends);

        return accountService.updateAccount(account);
    }

    private boolean hasFriends(Account loggedAccount) {
        return loggedAccount.getFriends() != null;
    }

    private boolean friendExist(Account loggedAccount, Account friendAccount) {
        return hasFriends(loggedAccount) && loggedAccount.getFriends().stream()
            .anyMatch(friend -> friend.getId().equals(friendAccount.getId()));
    }
}

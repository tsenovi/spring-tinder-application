package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.constants.AccountType;
import com.volasoftware.tinder.constants.OperationConstant;
import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.dtos.FriendSearchDto;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.FriendExistException;
import com.volasoftware.tinder.exceptions.FriendNotFoundException;
import com.volasoftware.tinder.exceptions.NoRealAccountsException;
import com.volasoftware.tinder.mapper.FriendMapper;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.services.contracts.FriendService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final AccountRepository accountRepository;

    private final Random random;

    private final FriendMapper friendMapper;

    @Override
    public void addFriend(Long friendId) {
        Account loggedAccount = getLoggedAccount();
        Account friendAccount = getAccountById(friendId);

        if (hasFriends(loggedAccount)) {
            if (friendExist(loggedAccount, friendAccount)) {
                throw new FriendExistException(AccountConstant.ALREADY_FRIEND);
            }
        } else {
            loggedAccount.setFriends(new HashSet<>());
        }

        loggedAccount.getFriends().add(friendAccount);
        accountRepository.save(loggedAccount);
    }

    @Override
    public void removeFriend(Long friendId) {
        Account loggedAccount = getLoggedAccount();
        Account friendAccount = getAccountById(friendId);

        if (hasFriends(loggedAccount) && friendExist(loggedAccount, friendAccount)) {
            loggedAccount.getFriends().remove(friendAccount);
            accountRepository.save(loggedAccount);
        } else {
            throw new FriendNotFoundException(AccountConstant.FRIEND_NOT_FOUND);
        }
    }

    @Override
    public List<FriendDto> sortFriendsByLocation(FriendSearchDto friendSearchDto) {
        Double accountLatitude = friendSearchDto.getLatitude();
        Double accountLongitude = friendSearchDto.getLongitude();
        Account loggedAccount = getLoggedAccount();

        List<Account> sortedFriends = accountRepository.findFriendsByLocation(
            loggedAccount.getId(), accountLatitude, accountLongitude);
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


    private String linkRequestedAccountWithBots(Long accountId, Pageable pageable) {
        Account realAccount = getAccountById(accountId);

        Set<Account> botSet = getAccountsByAccountType(AccountType.BOT, pageable);

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
        Set<Account> realAccounts = getAccountsByAccountType(AccountType.REAL, pageable);
        if (realAccounts.isEmpty()) {
            throw new NoRealAccountsException(AccountConstant.ACCOUNTS_NOT_EXIST);
        }

        Set<Account> botSet = getAccountsByAccountType(AccountType.BOT, pageable);

        int initialFriendsCount = getFriendsCount(realAccounts);
        int finalFriendsCount = 0;
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
        return account.getFriends() == null ? 0 : account.getFriends().size();
    }

    private int getFriendsCount(Set<Account> realAccounts) {
        return realAccounts
            .stream()
            .mapToInt(this::getFriendsCount)
            .sum();
    }

    private Account addBotFriendsToExistingSet(Account account, Set<Account> botAccounts) {
        Set<Account> botFriends = account.getFriends();
        int randomBotsCount = random.nextInt(botAccounts.size());
        List<Account> botAccountList = new ArrayList<>(botAccounts);
        botAccountList.removeIf(botFriends::contains);
        botFriends.addAll(botAccountList.subList(0, randomBotsCount));
        account.setFriends(botFriends);

        return accountRepository.save(account);
    }

    private Set<Account> getAccountsByAccountType(AccountType accountType, Pageable pageable) {

        return Stream.iterate(pageable, Pageable::next)
            .map(pageRequest -> accountRepository.findByAccountType(accountType, pageRequest))
            .takeWhile(page -> page != null && page.hasContent())
            .flatMap(page -> page.getContent().stream())
            .collect(Collectors.toSet());
    }

    private boolean hasFriends(Account loggedAccount) {
        return loggedAccount.getFriends() != null;
    }

    private boolean friendExist(Account loggedAccount, Account friendAccount) {
        return loggedAccount.getFriends().contains(friendAccount);
    }

    private Account getAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow(
            () -> new AccountNotFoundException(AccountConstant.NOT_FOUND)
        );
    }

    private Account getLoggedAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return accountRepository.findOneByEmail(username).orElseThrow(
            () -> new AccountNotFoundException(AccountConstant.NOT_FOUND)
        );
    }
}

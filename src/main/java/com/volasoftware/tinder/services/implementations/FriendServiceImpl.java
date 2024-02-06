package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.constants.AccountType;
import com.volasoftware.tinder.constants.OperationConstant;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.FriendExistException;
import com.volasoftware.tinder.exceptions.FriendNotFoundException;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.services.contracts.FriendService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final AccountRepository accountRepository;

    private final Random random;

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

    public String linkAllAccountsWithBots(Pageable pageable) {
        Set<Account> realAccounts = getAccountsByAccountType(AccountType.REAL, pageable);
        Set<Account> botSet = getAccountsByAccountType(AccountType.BOT, pageable);

        int initialFriendsCount = getFriendsCount(realAccounts);
        for (Account realAccount : realAccounts) {
            if (!hasFriends(realAccount)) {
                realAccount.setFriends(new HashSet<>());
            }

            addBotFriendsToExistingSet(realAccount, botSet);
        }

        int finalFriendsCount = getFriendsCount(realAccounts);
        if (finalFriendsCount > initialFriendsCount) {
            return OperationConstant.SUCCESSFUL;
        } else {
            throw new FriendExistException(OperationConstant.FAILED);
        }
    }

    private int getFriendsCount(Set<Account> realAccounts) {
        return realAccounts
            .stream()
            .mapToInt(account -> account.getFriends() == null ? 0 : account.getFriends().size())
            .sum();
    }

    private void addBotFriendsToExistingSet(Account account, Set<Account> botAccounts) {
        Set<Account> botFriends = account.getFriends();
        int randomBotsCount = random.nextInt(botAccounts.size());
        List<Account> botAccountList = new ArrayList<>(botAccounts);
        botAccountList.removeIf(botFriends::contains);
        botFriends.addAll(botAccountList.subList(0, randomBotsCount));

        accountRepository.save(account);
    }

    private Set<Account> getAccountsByAccountType(AccountType accountType, Pageable pageable) {
        Set<Account> accounts = new HashSet<>();
        Page<Account> page = accountRepository.findByAccountType(accountType, pageable);
        while (page.hasContent()) {
            accounts.addAll(page.getContent());
            pageable = page.nextPageable();
            page = accountRepository.findByAccountType(accountType, pageable);
        }

        return accounts;
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

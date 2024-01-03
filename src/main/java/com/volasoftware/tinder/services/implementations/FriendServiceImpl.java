package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.FriendExistException;
import com.volasoftware.tinder.exceptions.FriendNotFoundException;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.services.contracts.FriendService;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final AccountRepository accountRepository;

    @Override
    public void addFriend(Long friendId) {
        Account loggedAccount = getLoggedAccount();
        Account friendAccount = getAccountById(friendId);

        if (hasFriends(loggedAccount)) {
            if (friendExist(loggedAccount, friendAccount)) {
                throw new FriendExistException(AccountConstant.ALREADY_EXIST);
            }
        }

        HashSet<Account> friends = new HashSet<>();
        friends.add(friendAccount);
        loggedAccount.setFriends(friends);
        accountRepository.save(loggedAccount);
    }

    @Override
    public void removeFriend(Long friendId) {
        Account loggedAccount = getLoggedAccount();
        Account friendAccount = getAccountById(friendId);

        if (!hasFriends(loggedAccount)) {
            throw new FriendNotFoundException(AccountConstant.NOT_FOUND);
        } else if (friendExist(loggedAccount, friendAccount)) {
            loggedAccount.getFriends().remove(friendAccount);
            accountRepository.save(loggedAccount);
        }
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

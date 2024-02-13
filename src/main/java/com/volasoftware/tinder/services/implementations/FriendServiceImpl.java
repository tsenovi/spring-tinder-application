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
import com.volasoftware.tinder.models.Location;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.services.contracts.FriendService;
import java.util.ArrayList;
import java.util.Comparator;
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
        Set<Account> friends = loggedAccount.getFriends();

        Comparator<Account> comparator = Comparator.comparingDouble(
            friend -> {
                if (friend.getLocation() == null) {
                    return Double.MAX_VALUE;
                }

                Location friendLocation = friend.getLocation();
                Double friendLongitude = friendLocation.getLongitude();
                Double friendLatitude = friendLocation.getLatitude();

                return calculateDistance(
                    accountLatitude, accountLongitude, friendLatitude, friendLongitude);
            }
        );

        ArrayList<Account> sortedFriends = new ArrayList<>(friends);
        sortedFriends.sort(comparator);

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

    //Haversine formula used to calculate the distance between two points on Earth's surface
    private double calculateDistance(
        Double accountLongitude, Double accountLatitude,
        Double friendLongitude, Double friendLatitude) {

        // Convert latitude and longitude values from degrees to radians
        double latitudeDeltaRadians = Math.toRadians(friendLatitude - accountLatitude);
        double longitudeDeltaRadians = Math.toRadians(friendLongitude - accountLongitude);

        // Calculate the 'a' value using the 'sin' and 'cos' values from the deltas
        double earthRadius = 6371000;
        double earthRadiusSquared = earthRadius * earthRadius;
        double a = Math.sin(latitudeDeltaRadians / 2) * Math.sin(latitudeDeltaRadians / 2) +
            Math.cos(Math.toRadians(accountLatitude)) * Math.cos(Math.toRadians(friendLatitude)) *
                Math.sin(longitudeDeltaRadians / 2) * Math.sin(longitudeDeltaRadians / 2);

        // Calculate the 'c' value using the 'a' value and the Earth's radius
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Return the final distance by multiplying 'c' by the Earth's radius
        return earthRadius * c;
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

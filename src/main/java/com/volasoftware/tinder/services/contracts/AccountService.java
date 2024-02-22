package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.constants.AccountType;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.models.Account;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;

public interface AccountService {

    List<AccountDto> getAccounts(Pageable pageable);

    AccountDto getAccountDtoByEmail(String email);

    List<Account> getFriendsByLocation(Account loggedAccount, Double accountLatitude,
        Double accountLongitude);

    Account getAccountById(Long id);

    Account getLoggedAccount();

    Set<Account> getAccountsByType(AccountType accountType, Pageable pageable);

    Account updateAccount(Account account);

    void checkIfEmailIsTaken(String email);

    Account getAccountByEmail(String email);
}

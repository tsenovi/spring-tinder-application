package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.AccountConstant;
import com.volasoftware.tinder.constants.AccountType;
import com.volasoftware.tinder.constants.MailConstant;
import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.EmailIsTakenException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.services.contracts.AccountService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    @Override
    public List<AccountDto> getAccounts(Pageable pageable) {
        Page<Account> accountsPage = accountRepository.findAll(pageable);
        List<Account> accountsList = new ArrayList<>();

        while (!accountsPage.isEmpty()) {
            accountsPage.forEach(accountsList::add);

            accountsPage = accountRepository.findAll(pageable.next());
        }

        return accountMapper.accountListToAccountDtoList(accountsList);
    }

    @Override
    public AccountDto getAccountDtoByEmail(String email) {
        Account account = getAccountByEmail(email);
        return accountMapper.accountToAccountDto(account);
    }

    @Override
    public List<Account> getFriendsByLocation(Account loggedAccount, Double accountLatitude,
        Double accountLongitude) {
        return accountRepository.findFriendsByLocation(
            loggedAccount.getId(), accountLatitude, accountLongitude);
    }

    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow(
            () -> new AccountNotFoundException(AccountConstant.NOT_FOUND)
        );
    }

    @Override
    public Account getLoggedAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return getAccountByEmail(username);
    }

    @Override
    public Set<Account> getAccountsByType(AccountType accountType, Pageable pageable) {

        return Stream.iterate(pageable, Pageable::next)
            .map(pageRequest -> accountRepository.findByAccountType(accountType, pageRequest))
            .takeWhile(page -> page != null && page.hasContent())
            .flatMap(page -> page.getContent().stream())
            .collect(Collectors.toSet());
    }

    @Override
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public void checkIfEmailIsTaken(String email) {
        Optional<Account> optionalAccount = accountRepository.findOneByEmail(email);
        if (optionalAccount.isPresent()) {
            throw new EmailIsTakenException(MailConstant.ALREADY_TAKEN);
        }
    }

    @Override
    public Account getAccountByEmail(String email) {
        return accountRepository.findOneByEmail(email)
            .orElseThrow(() -> new AccountNotFoundException(AccountConstant.NOT_FOUND));
    }
}

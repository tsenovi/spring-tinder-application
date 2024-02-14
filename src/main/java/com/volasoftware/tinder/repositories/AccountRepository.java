package com.volasoftware.tinder.repositories;

import com.volasoftware.tinder.constants.AccountType;
import com.volasoftware.tinder.models.Account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findOneByEmail(String email);

    @NonNull
    Page<Account> findAll(@NonNull Pageable pageable);

    Page<Account> findByAccountType(@NonNull AccountType accountType, @NonNull Pageable pageable);

    @Query(
        "SELECT f FROM Account a "
            + "JOIN a.friends f "
            + "WHERE a.id = :accountId AND f.location IS NOT NULL "
            + "ORDER BY "
            + "(6371 * acos(cos(radians(:latitude)) * cos(radians(f.location.latitude)) * "
            + "cos(radians(f.location.longitude) - radians(:longitude)) + sin(radians(:latitude)) * "
            + "sin(radians(f.location.latitude))))")
    List<Account> findFriendsByLocation(@Param("accountId") Long accountId,
        @Param("latitude") Double latitude, @Param("longitude") Double longitude);
}

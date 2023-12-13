package com.volasoftware.tinder.repositories;

import com.volasoftware.tinder.models.Account;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  Optional<Account> findOneByEmail(String email);

  @NonNull
  Page<Account> findAll(@NonNull Pageable pageable);
}

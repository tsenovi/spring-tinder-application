package com.volasoftware.tinder.repositories;


import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.Rating;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Account> findByFriendId(Long id);
}

package com.volasoftware.tinder.services.contracts;

import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface FriendService {

    void addFriend(Long friendId);

    void removeFriend(Long friendId);

    String linkAllAccountsWithBots(Pageable pageable);
}

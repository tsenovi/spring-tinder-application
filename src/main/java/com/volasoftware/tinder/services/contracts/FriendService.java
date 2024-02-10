package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.dtos.FriendSearchDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface FriendService {

    void addFriend(Long friendId);

    void removeFriend(Long friendId);

    String linkAllAccountsWithBots(Pageable pageable);

    String linkRequestedAccountWithBots(Long accountId, Pageable pageable);

    List<FriendDto> sortFriendsByLocation(FriendSearchDto friendSearchDto);
}

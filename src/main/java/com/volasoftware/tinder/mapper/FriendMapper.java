package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.dtos.FriendDto;
import com.volasoftware.tinder.models.Account;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = LocationMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FriendMapper {

    @Mapping(source = "location", target = "locationDto")
    FriendDto accountToFriendDto(Account account);

    List<FriendDto> accountListToFriendDtoList(List<Account> accounts);
}

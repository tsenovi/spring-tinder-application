package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.dtos.RegisterRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

  AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "lastModifiedDate", ignore = true)
  @Mapping(target = "id", ignore = true)
  Account registerRequestToAccount(RegisterRequest registerRequest);

  AccountDTO accountToAccountDto(Account account);

  List<AccountDTO> accountListToAccountDtoList(List<Account> list);
}

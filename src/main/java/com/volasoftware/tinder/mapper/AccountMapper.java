package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.dtos.RegisterRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "lastModifiedDate", ignore = true)
  @Mapping(target = "id", ignore = true)
  Account registerRequestToAccount(RegisterRequest registerRequest);

  AccountDto accountToAccountDto(Account account);

  List<AccountDto> accountListToAccountDtoList(List<Account> list);
}

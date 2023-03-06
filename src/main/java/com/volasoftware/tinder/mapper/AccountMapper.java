package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.dtos.AccountDTO;
import com.volasoftware.tinder.dtos.RegisterDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {

  AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "lastModifiedDate", ignore = true)
  @Mapping(target = "id", ignore = true)
  Account registerDtoToAccount(RegisterDTO registerDTO);

  AccountDTO accountToAccountDto(Account account);

  List<AccountDTO> accountListToAccountDtoList(List<Account> list);
}

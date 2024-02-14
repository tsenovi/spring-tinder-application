package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.dtos.LocationDto;
import com.volasoftware.tinder.models.Location;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {

    LocationDto locationToLocationDto(Location location);

    List<LocationDto> locationListToLocationDtoList(List<Location> locations);
}

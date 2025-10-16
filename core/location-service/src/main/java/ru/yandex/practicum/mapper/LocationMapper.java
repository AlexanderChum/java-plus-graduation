package ru.yandex.practicum.mapper;

import main.server.location.LocationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDto toDto(Location location);

    @Mapping(target = "id", ignore = true)
    Location toEntity(LocationDto locationDto);
}

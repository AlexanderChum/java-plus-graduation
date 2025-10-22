package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.category.dtos.CategoryDto;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.EventShortDto;
import ru.yandex.practicum.event.dtos.NewEventDto;
import ru.yandex.practicum.location.dtos.LocationDto;
import ru.yandex.practicum.model.EventModel;
import ru.yandex.practicum.users.dtos.UserShortDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoryId", source = "category")
    @Mapping(target = "initiatorId", source = "initiator")
    @Mapping(target = "locationId", source = "location")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "confirmedRequests", constant = "0L")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)
    EventModel toEntity(NewEventDto dto, Long category, Long initiator, Long location);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "categoryDto", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "locationDto", source = "location")
    @Mapping(target = "views", ignore = true)
    EventFullDto toFullDto(EventModel entity, CategoryDto category, UserShortDto initiator, LocationDto location);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "views", ignore = true)
    EventShortDto toShortDto(EventModel entity, CategoryDto category, UserShortDto initiator);
}

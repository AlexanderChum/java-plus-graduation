package main.server.events.mapper;

import main.server.category.mapper.CategoryMapper;
import main.server.category.model.Category;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.EventShortDto;
import ru.yandex.practicum.event.dtos.NewEventDto;
import main.server.events.model.EventModel;
import main.server.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "annotation", source = "dto.annotation")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "eventDate", source = "dto.eventDate")
    @Mapping(target = "paid", source = "dto.paid")
    @Mapping(target = "participantLimit", source = "dto.participantLimit")
    @Mapping(target = "requestModeration", source = "dto.requestModeration")
    @Mapping(target = "title", source = "dto.title")

    // фиксируем первоначальные поля
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "confirmedRequests", constant = "0L")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)

    // объекты
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "user")
    EventModel toEntity(NewEventDto dto,
                        Category category,
                        User user);

    @Mapping(source = "category", target = "categoryDto")
    @Mapping(target = "locationDto", ignore = true)
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(target = "views", ignore = true)
    EventFullDto toFullDto(EventModel entity);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(target = "views", ignore = true)
    EventShortDto toShortDto(EventModel entity);
}

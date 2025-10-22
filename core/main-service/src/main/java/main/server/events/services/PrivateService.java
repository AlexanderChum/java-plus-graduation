package main.server.events.services;

import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.EventShortDto;
import ru.yandex.practicum.event.dtos.NewEventDto;
import ru.yandex.practicum.event.dtos.UpdateEventUserRequest;

import java.util.List;

public interface PrivateService {

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    EventFullDto getEventByEventId(Long userId, Long eventId);

    EventFullDto updateEventByEventId(UpdateEventUserRequest updateEventDto, Long userId, Long eventId);
}

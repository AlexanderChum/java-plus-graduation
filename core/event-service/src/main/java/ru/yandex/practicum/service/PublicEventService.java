package ru.yandex.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {
    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    List<EventShortDto> getEventsWithFilters(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                             Integer size, HttpServletRequest request);

    EventFullDto getEventByIdForRequest(Long eventId);

    boolean checkInitiatorAndEventIds(Long eventId, Long userId);
}

package ru.yandex.practicum.controllers;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.PrivateEventFeignClient;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.EventShortDto;
import ru.yandex.practicum.event.dtos.NewEventDto;
import ru.yandex.practicum.event.dtos.UpdateEventUserRequest;
import ru.yandex.practicum.service.PrivateEventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateEventController implements PrivateEventFeignClient {
    PrivateEventService service;

    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@PathVariable
                                             @Positive
                                             Long userId,

                                             @RequestParam(defaultValue = "0")
                                             @PositiveOrZero
                                             Integer from,

                                             @RequestParam(defaultValue = "10")
                                             @Positive
                                             Integer size) {
        log.info("Получение событий, добавленных текущим пользователем");
        return service.getUserEvents(userId, from, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
        log.info("Добавление нового события пользователем");
        return service.createEvent(newEventDto, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByEventIdAndUserId(Long userId, Long eventId) {
        log.info("Получение полной информации о событии добавленном текущим пользователем");
        return service.getEventByEventId(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByEventId(Long userId, Long eventId, UpdateEventUserRequest updateEventDto) {
        log.info("Изменение события, добавленного текущим пользователем");
        return service.updateEventByEventId(updateEventDto, userId, eventId);
    }
}

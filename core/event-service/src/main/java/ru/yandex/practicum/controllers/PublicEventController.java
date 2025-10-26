package ru.yandex.practicum.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.PublicEventFeignClient;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.EventShortDto;
import ru.yandex.practicum.service.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

import static stat.constant.Const.DATE_TIME_FORMAT;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicEventController {
    PublicEventService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,

                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                         LocalDateTime rangeStart,

                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                         LocalDateTime rangeEnd,

                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,

                                         @RequestParam(required = false)
                                         @Pattern(regexp = "EVENT_DATE|VIEWS")
                                         String sort,

                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        log.info("Поступил запрос на получение событий от ноунейма");
        return service.getEventsWithFilters(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable @Positive Long eventId,
                                     HttpServletRequest request) {
        log.info("Поступил запрос на получение события по id от ноунейма");
        return service.getEventById(eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventForRequestService(@PathVariable @Positive Long eventId) {
        log.info("Поступил запрос на получение события для реквеста");
        return service.getEventByIdForRequest(eventId);
    }

    @GetMapping("{eventId}/request/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean checkInitiatorAndEventIds(@PathVariable @Positive Long eventId,
                                             @PathVariable @Positive Long userId) {
        log.info("Поступил запрос для проверки id события и пользователя");
        return service.checkInitiatorAndEventIds(eventId, userId);
    }
}

package ru.yandex.practicum.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.UpdateEventAdminRequest;
import ru.yandex.practicum.service.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

import static stat.constant.Const.DATE_TIME_FORMAT;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminEventController {
    AdminEventService service;

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                                    @PathVariable @Positive Long eventId) {
        log.info("Получен запрос на обновление события у админа");
        return service.updateEvent(updateEventAdminRequest, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsWithAdminFilters(@RequestParam(required = false) List<Long> users,
                                                        @RequestParam(required = false) List<String> states,
                                                        @RequestParam(required = false) List<Long> categories,

                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                                        LocalDateTime rangeStart,

                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                                        LocalDateTime rangeEnd,

                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос на обновление события для админа");
        return service.getEventsWithAdminFilters(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}

package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.request.PrivateEventRequestFeignClient;
import ru.yandex.practicum.request.dtos.EventRequestStatusUpdateRequestDto;
import ru.yandex.practicum.request.dtos.EventRequestStatusUpdateResultDto;
import ru.yandex.practicum.request.dtos.ParticipationRequestDto;
import ru.yandex.practicum.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateEventRequestController implements PrivateEventRequestFeignClient {
    RequestService service;

    @Override
    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequestsByOwner(@PathVariable @Positive Long userId,
                                                                 @PathVariable @Positive Long eventId) {
        log.info("Получение информации о запросах на участие в событии текущего пользователя");
        return service.getCurrentUserEventRequests(userId, eventId);
    }

    @Override
    @PatchMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResultDto updateEventRequest(@PathVariable @Positive Long userId,
                                                                @PathVariable @Positive Long eventId,
                                                                @RequestBody @Valid
                                                                EventRequestStatusUpdateRequestDto update) {
        log.info("Изменение статуса заявок на участие в событии текущего пользователя");
        return service.updateParticipationRequestsStatus(userId, eventId, update);
    }
}
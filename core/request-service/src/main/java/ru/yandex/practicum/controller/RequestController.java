package ru.yandex.practicum.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.request.RequestFeignClient;
import ru.yandex.practicum.request.dtos.ParticipationRequestDto;
import ru.yandex.practicum.service.RequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestController implements RequestFeignClient {
    RequestService service;

    @Override
    @GetMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable @NotNull @Positive Long userId) {
        log.info("Получаем запросы");
        return service.getRequests(userId);
    }

    @Override
    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable @NotNull @Positive Long userId,
                                                 @RequestParam("eventId") @NotNull @Positive Long eventId) {
        log.info("Создаем запрос id={}", userId);
        return service.createRequest(userId, eventId);
    }

    @Override
    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable @NotNull @Positive Long userId,
                                                 @PathVariable("requestId") @NotNull @Positive Long requestId) {
        log.info("Отменяем запрос");
        return service.cancelRequest(userId, requestId);
    }
}

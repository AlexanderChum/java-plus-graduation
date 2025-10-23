package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.request.RequestFeignClient;
import ru.yandex.practicum.request.dtos.ParticipationRequestDto;
import ru.yandex.practicum.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestController implements RequestFeignClient {
    RequestService service;

    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(Long requesterId) {
        log.info("Получаем запросы");
        return service.getRequests(requesterId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(Long requesterId, Long eventId) {
        log.info("Создаем запрос id={}", requesterId);
        return service.createRequest(requesterId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(Long requesterId, Long requestId) {
        log.info("Отменяем запрос");
        return service.cancelRequest(requesterId, requestId);
    }
}

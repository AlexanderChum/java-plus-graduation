package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.location.LocationFeignClient;
import ru.yandex.practicum.location.dtos.LocationDto;
import ru.yandex.practicum.service.LocationService;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationControllerClient implements LocationFeignClient {
    LocationService service;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto createLocation(LocationDto dto) {
        log.info("Получен запрос на добавление локации");
        return service.createLocation(dto);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public LocationDto getLocation(Long locationId) {
        log.info("");
        return service.getLocation(locationId);
    }
}

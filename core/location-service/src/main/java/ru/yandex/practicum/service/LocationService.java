package ru.yandex.practicum.service;

import ru.yandex.practicum.location.dtos.LocationDto;

public interface LocationService {

    LocationDto createLocation(LocationDto dto);

    LocationDto getLocation(Long locationId);
}

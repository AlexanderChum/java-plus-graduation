package ru.yandex.practicum.service;

import main.server.location.LocationDto;

public interface LocationService {

    LocationDto createLocation(LocationDto dto);

    LocationDto createLocation(Double lat, Double lon);

    LocationDto getLocation(Long locationId);
}

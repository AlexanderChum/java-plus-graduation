package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.server.exception.NotFoundException;
import main.server.location.LocationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mapper.LocationMapper;
import ru.yandex.practicum.model.Location;
import ru.yandex.practicum.repository.LocationRepository;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {
    LocationRepository repository;
    LocationMapper mapper;

    @Override
    public LocationDto createLocation(LocationDto locationDto) {
        log.info("Запрос в сервис на добавление локации");
        Location location = repository.save(mapper.toEntity(locationDto));
        log.debug("Мужик, есть локация? На, мужик, локацию: {}, {}", location.getLat(), location.getLon());
        return mapper.toDto(location);
    }

    @Override
    public LocationDto createLocation(Double lat, Double lon) {
        log.info("Запрос в сервис на добавление локации");
        LocationDto dto = new LocationDto(lat, lon);
        Location location = repository.save(mapper.toEntity(dto));
        log.debug("Мужик, я снова с локацией, держи: {}, {}", location.getLat(), location.getLon());
        return mapper.toDto(location);
    }

    @Override
    public LocationDto getLocation(Long locationId) {
        log.info("Запрос в сервис на получение локации");
        Location location = repository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Локация с таким id " + locationId + " не найдена"));
        return mapper.toDto(location);
    }
}

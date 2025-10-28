package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.errors.exceptions.NotFoundException;
import ru.yandex.practicum.location.dtos.LocationDto;
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
    @Transactional(readOnly = true)
    public LocationDto getLocation(Long locationId) {
        log.info("Запрос в сервис на получение локации");
        Location location = repository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Локация с таким id " + locationId + " не найдена"));
        return mapper.toDto(location);
    }
}

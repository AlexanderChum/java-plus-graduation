package ru.yandex.practicum.service.impls;

import client.StatsClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.CategoryFeignClient;
import ru.yandex.practicum.category.dtos.CategoryDto;
import ru.yandex.practicum.errors.exceptions.BadRequestException;
import ru.yandex.practicum.errors.exceptions.NotFoundException;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.EventShortDto;
import ru.yandex.practicum.event.enums.EventState;
import ru.yandex.practicum.location.LocationFeignClient;
import ru.yandex.practicum.location.dtos.LocationDto;
import ru.yandex.practicum.mapper.EventMapper;
import ru.yandex.practicum.model.EventModel;
import ru.yandex.practicum.repository.EventRepository;
import ru.yandex.practicum.service.PublicEventService;
import ru.yandex.practicum.users.UsersFeignClient;
import ru.yandex.practicum.users.dtos.UserShortDto;
import stat.dto.EndpointHitDto;
import stat.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicEventServiceImpl implements PublicEventService {
    EventMapper mapper;
    EventRepository repository;

    StatsClient statsClient;
    CategoryFeignClient categoryClient;
    UsersFeignClient usersClient;
    LocationFeignClient locationClient;

    public List<EventShortDto> getEventsWithFilters(String text, List<Long> categories, Boolean paid,
                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                    Boolean onlyAvailable, String sort, Integer from,
                                                    Integer size, HttpServletRequest request) {

        log.debug("Получен запрос на получение public событий с фильтрами");
        if ((rangeStart != null) && (rangeEnd != null) && (rangeStart.isAfter(rangeEnd)))
            throw new BadRequestException("Время начала на может быть позже окончания");

        List<EventModel> events = repository.findAllByFiltersPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, PageRequest.of(from, size));

        try {
            statsClient.postHit(EndpointHitDto.builder()
                    .app("main-service")
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.error("Не удалось отправить запрос о сохранении на сервер статистики");
        }

        Map<Long, Long> views = getAmountOfViews(events);
        log.debug("Собираем события для ответа");

        return events.stream()
                .map(eventModel -> {
                    CategoryDto categoryDto = categoryClient.getCategoryById(eventModel.getCategoryId());
                    UserShortDto userDto = usersClient.getUserById(eventModel.getInitiatorId());
                    EventShortDto eventShort = mapper.toShortDto(eventModel, categoryDto, userDto);
                    eventShort.setViews(views.getOrDefault(eventModel.getId(), 0L));
                    return eventShort;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {

        log.debug("Получен запрос на получение события по id");
        EventModel event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id= %d не было найдено", eventId)));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException(String.format("Событие с id= %d недоступно, так как не опубликовано", eventId));
        }

        try {
            statsClient.postHit(EndpointHitDto.builder()
                    .app("main-service")
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.error("Не удалось отправить запрос о сохранении на сервер статистики");
        }

        log.debug("Собираем событие для ответа");
        CategoryDto categoryDto = categoryClient.getCategoryById(event.getCategoryId());
        UserShortDto userDto = usersClient.getUserById(event.getInitiatorId());
        LocationDto locationDto = locationClient.getLocation(event.getLocationId());
        EventFullDto result = mapper.toFullDto(event, categoryDto, userDto, locationDto);
        Map<Long, Long> views = getAmountOfViews(List.of(event));
        result.setViews(views.getOrDefault(event.getId(), 0L));

        return result;
    }

    public EventFullDto getEventByIdForRequest(Long eventId) {
        log.debug("Получен запрос на получение события по id");
        EventModel event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id= %d не было найдено", eventId)));

        //if (event.getState() != EventState.PUBLISHED) {
        //    throw new NotFoundException(String.format("Событие с id= %d недоступно, так как не опубликовано", eventId));
        //}

        log.debug("Собираем событие для ответа");
        CategoryDto categoryDto = categoryClient.getCategoryById(event.getCategoryId());
        UserShortDto userDto = usersClient.getUserById(event.getInitiatorId());
        LocationDto locationDto = locationClient.getLocation(event.getLocationId());
        EventFullDto result = mapper.toFullDto(event, categoryDto, userDto, locationDto);
        Map<Long, Long> views = getAmountOfViews(List.of(event));
        result.setViews(views.getOrDefault(event.getId(), 0L));

        return result;
    }

    public boolean checkInitiatorAndEventIds(Long eventId, Long userId) {
        log.info("Проверка для реквеста");
        return repository.existsByIdAndInitiatorId(eventId, userId);
    }

    public boolean checkEventsByCategoryId(Long categoryId) {
        log.info("Получение событий для сервиса категорий");
        List<EventModel> events = repository.findAllByCategoryId(categoryId);
        return !events.isEmpty();
    }

    private Map<Long, Long> getAmountOfViews(List<EventModel> events) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyMap();
        }
        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .distinct()
                .collect(Collectors.toList());

        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(5);

        Map<Long, Long> viewsMap = new HashMap<>();
        try {
            log.debug("Получение статистики по времени для URI: {} с {} по {}", uris, startTime, endTime);
            List<ViewStatsDto> stats = statsClient.getStatistics(
                    startTime,
                    endTime,
                    uris,
                    true
            );
            log.debug("Получение статистики");
            if (stats != null && !stats.isEmpty()) {
                for (ViewStatsDto stat : stats) {
                    Long eventId = Long.parseLong(stat.getUri().substring("/events/".length()));
                    viewsMap.put(eventId, stat.getHits());
                }
            }
        } catch (Exception e) {
            log.error("Не удалось получить статистику");
        }
        return viewsMap;
    }
}

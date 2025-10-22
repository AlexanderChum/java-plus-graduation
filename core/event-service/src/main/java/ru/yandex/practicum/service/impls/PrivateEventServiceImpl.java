package ru.yandex.practicum.service.impls;

import client.StatsClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.CategoryFeignClient;
import ru.yandex.practicum.category.dtos.CategoryDto;
import ru.yandex.practicum.errors.exceptions.BadRequestException;
import ru.yandex.practicum.errors.exceptions.ConflictException;
import ru.yandex.practicum.errors.exceptions.NotFoundException;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.EventShortDto;
import ru.yandex.practicum.event.dtos.NewEventDto;
import ru.yandex.practicum.event.dtos.UpdateEventUserRequest;
import ru.yandex.practicum.event.enums.EventState;
import ru.yandex.practicum.event.enums.StateAction;
import ru.yandex.practicum.location.LocationFeignClient;
import ru.yandex.practicum.location.dtos.LocationDto;
import ru.yandex.practicum.mapper.EventMapper;
import ru.yandex.practicum.model.EventModel;
import ru.yandex.practicum.repository.EventRepository;
import ru.yandex.practicum.service.PrivateEventService;
import ru.yandex.practicum.users.UsersFeignClient;
import ru.yandex.practicum.users.dtos.UserShortDto;
import stat.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateEventServiceImpl implements PrivateEventService {
    EventMapper mapper;
    EventRepository repository;

    StatsClient statsClient;
    UsersFeignClient userClient;
    CategoryFeignClient categoryClient;
    LocationFeignClient locationClient;

    public EventFullDto createEvent(NewEventDto newEvent, Long userId) {
        log.debug("Получен запрос на создание нового события");
        UserShortDto userDto = userClient.getUserById(userId); //если пользователя нет - клиент выкинет 404
        CategoryDto categoryDto = categoryClient.getCategoryById(newEvent.getCategory());
        LocationDto locationDto = locationClient.createLocation(newEvent.getLocationDto());

        EventModel event = mapper.toEntity(newEvent, categoryDto.getId(), userId, locationDto.getId());
        log.debug("");
        return mapper.toFullDto(repository.save(event), categoryDto, userDto, locationDto);
    }

    public EventFullDto updateEventByEventId(UpdateEventUserRequest update, Long userId, Long eventId) {
        log.debug("Получен запрос на обновление события пользователем");
        userClient.getUserById(userId);
        EventModel event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие  c id= %d не найдено", eventId)));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Невозможно обновить опубликованное событие");
        }

        if (update.getEventDate() != null && update.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Изменяемая дата не может быть в прошлом");
        }

        changeEventState(event, update);
        updateEventFields(event, update);

        log.debug("Сборка события для ответа");

        UserShortDto userDto = userClient.getUserById(userId);
        CategoryDto categoryDto = categoryClient.getCategoryById(event.getCategoryId());
        LocationDto locationDto = locationClient.getLocation(event.getLocationId());
        EventFullDto result = mapper.toFullDto(event, categoryDto, userDto, locationDto);
        result.setViews(getAmountOfViews(List.of(event)).get(eventId));
        return result;
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.debug("Получен запрос для получения событий пользователя");
        userClient.getUserById(userId);

        Page<EventModel> events = repository.findByInitiatorId(
                userId,
                PageRequest.of(from / size, size, Sort.by("eventDate").descending())
        );

        Map<Long, Long> views = getAmountOfViews(events.getContent());
        return events.getContent().stream()
                .map(event -> {
                    UserShortDto userDto = userClient.getUserById(event.getInitiatorId());
                    CategoryDto categoryDto = categoryClient.getCategoryById(event.getCategoryId());
                    EventShortDto dto = mapper.toShortDto(event, categoryDto, userDto);
                    dto.setViews(views.getOrDefault(event.getId(), 0L));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullDto getEventByEventId(Long userId, Long eventId) {
        log.debug("Получен запрос события по id");
        userClient.getUserById(userId);
        EventModel event = repository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id= %d " +
                        "у пользователя с id= %d не найдено", eventId, userId)));

        log.debug("Сборка события для ответа");
        UserShortDto userDto = userClient.getUserById(userId);
        CategoryDto categoryDto = categoryClient.getCategoryById(event.getCategoryId());
        LocationDto locationDto = locationClient.getLocation(event.getLocationId());
        EventFullDto result = mapper.toFullDto(event, categoryDto, userDto, locationDto);
        result.setViews(getAmountOfViews(List.of(event)).get(eventId));
        return result;
    }

    private void changeEventState(EventModel event, UpdateEventUserRequest update) {
        if (update.getState() != null) {
            if (update.getState() == StateAction.SEND_TO_REVIEW) event.setState(EventState.PENDING);
            if (update.getState() == StateAction.CANCEL_REVIEW) event.setState(EventState.CANCELED);
        }
    }

    private void updateEventFields(EventModel event, UpdateEventUserRequest update) {
        if (update.getAnnotation() != null) {
            event.setAnnotation(update.getAnnotation());
        }

        if (update.getCategory() != null) {
            event.setCategoryId(update.getCategory());
        }

        if (update.getDescription() != null) {
            event.setDescription(update.getDescription());
        }

        if (update.getEventDate() != null) {
            if (update.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException("Событие не может быть раньше чем через 2 часа");
            }
            event.setEventDate(update.getEventDate());
        }

        if (update.getPaid() != null) {
            event.setPaid(update.getPaid());
        }

        if (update.getParticipantLimit() != null) {
            event.setParticipantLimit(update.getParticipantLimit());
        }

        if (update.getRequestModeration() != null) {
            event.setRequestModeration(update.getRequestModeration());
        }

        if (update.getTitle() != null) {
            event.setTitle(update.getTitle());
        }

        if (update.getLocation() != null) {
            LocationDto locationDto = locationClient.createLocation(update.getLocation());
            event.setLocationId(locationDto.getId());
        }
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

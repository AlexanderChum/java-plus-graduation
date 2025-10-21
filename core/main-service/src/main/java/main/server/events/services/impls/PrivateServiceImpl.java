package main.server.events.services.impls;

import client.StatsClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.server.category.model.Category;
import main.server.category.service.CategoryServiceImpl;
import main.server.events.dto.EventFullDto;
import main.server.events.dto.EventShortDto;
import main.server.events.dto.NewEventDto;
import main.server.events.dto.UpdateEventUserRequest;
import main.server.events.enums.EventState;
import main.server.events.enums.StateAction;
import main.server.events.mapper.EventMapper;
import main.server.events.model.EventModel;
import main.server.events.repository.EventRepository;
import main.server.events.services.PrivateService;
import ru.yandex.practicum.errors.exceptions.BadRequestException;
import ru.yandex.practicum.errors.exceptions.ConflictException;
import main.server.user.model.User;
import main.server.user.service.UserServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.errors.exceptions.NotFoundException;
import ru.yandex.practicum.location.LocationFeignClient;
import ru.yandex.practicum.location.dtos.LocationDto;
import ru.yandex.practicum.mapper.LocationMapper;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class PrivateServiceImpl implements PrivateService {
    EventRepository eventRepository;
    EventMapper eventMapper;
    UserServiceImpl userService;
    CategoryServiceImpl categoryService;
    StatsClient statsClient;

    LocationFeignClient locationClient;
    LocationMapper locationMapper;

    @Transactional
    public EventFullDto createEvent(NewEventDto newEvent, Long userId) {
        log.debug("Получен запрос на создание нового события");
        User user = userExistence(userId);
        Category category = categoryExistence(newEvent.getCategory());

        Long locationId = locationClient.createLocation(newEvent.getLocationDto()).getId();

        EventModel event = eventMapper.toEntity(newEvent, category, user);
        event.setLocationId(locationId);

        EventFullDto result = eventMapper.toFullDto(eventRepository.save(event));
        result.setLocationDto(locationClient.getLocation(locationId));
        return result;
    }

    @Transactional
    public EventFullDto updateEventByEventId(UpdateEventUserRequest update, Long userId, Long eventId) {
        log.debug("Получен запрос на обновление события пользователем");
        userExistence(userId);
        EventModel event = eventRepository.findById(eventId)
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

        EventFullDto result = eventMapper.toFullDto(event);
        result.setLocationDto(locationClient.getLocation(event.getLocationId()));
        result.setViews(getAmountOfViews(List.of(event)).get(eventId));
        return result;
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.debug("Получен запрос для получения событий пользователя");
        userExistence(userId);

        Page<EventModel> events = eventRepository.findByInitiatorId(
                userId,
                PageRequest.of(from / size, size, Sort.by("eventDate").descending())
        );

        Map<Long, Long> views = getAmountOfViews(events.getContent());
        return events.getContent().stream()
                .map(event -> {
                    EventShortDto dto = eventMapper.toShortDto(event);
                    dto.setViews(views.getOrDefault(event.getId(), 0L));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullDto getEventByEventId(Long userId, Long eventId) {
        log.debug("Получен запрос события по id");
        userExistence(userId);
        EventModel event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id= %d " +
                        "у пользователя с id= %d не найдено", eventId, userId)));

        log.debug("Сборка события для ответа");
        EventFullDto result = eventMapper.toFullDto(event);
        result.setLocationDto(locationClient.getLocation(event.getLocationId()));
        result.setViews(getAmountOfViews(List.of(event)).get(eventId));
        return result;
    }

    private User userExistence(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id= %d не найден", userId)));
    }

    private Category categoryExistence(Long categoryId) {
        return categoryService.findById(categoryId);
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
            Category category = categoryExistence(update.getCategory());
            event.setCategory(category);
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

    public List<EventModel> findAllById(List<Long> ids) {
        return eventRepository.findAllById(ids);
    }

    public EventModel findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие не было найдено"));
    }
}

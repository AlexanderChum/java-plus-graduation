package ru.yandex.practicum.service.impls;

import client.AnalyzerClient;
import client.CollectorClient;
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
import ru.yandex.practicum.grpc.stats.eventPredictions.RecommendedEventProto;
import ru.yandex.practicum.location.LocationFeignClient;
import ru.yandex.practicum.location.dtos.LocationDto;
import ru.yandex.practicum.mapper.EventMapper;
import ru.yandex.practicum.model.EventModel;
import ru.yandex.practicum.repository.EventRepository;
import ru.yandex.practicum.request.RequestFeignClient;
import ru.yandex.practicum.service.PublicEventService;
import ru.yandex.practicum.users.UsersFeignClient;
import ru.yandex.practicum.users.dtos.UserShortDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicEventServiceImpl implements PublicEventService {
    EventMapper mapper;
    EventRepository repository;

    CategoryFeignClient categoryClient;
    UsersFeignClient usersClient;
    LocationFeignClient locationClient;
    AnalyzerClient analyzerClient;
    CollectorClient collectorClient;
    RequestFeignClient requestClient;

    public List<EventShortDto> getEventsWithFilters(String text, List<Long> categories, Boolean paid,
                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                    Boolean onlyAvailable, String sort, Integer from,
                                                    Integer size, HttpServletRequest request) {

        log.debug("Получен запрос на получение public событий с фильтрами");
        if ((rangeStart != null) && (rangeEnd != null) && (rangeStart.isAfter(rangeEnd)))
            throw new BadRequestException("Время начала на может быть позже окончания");

        List<EventModel> events = repository.findAllByFiltersPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, PageRequest.of(from, size));

        log.debug("Собираем события для ответа");

        return events.stream()
                .map(eventModel -> {
                    CategoryDto categoryDto = categoryClient.getCategoryById(eventModel.getCategoryId());
                    UserShortDto userDto = usersClient.getUserById(eventModel.getInitiatorId());
                    EventShortDto eventShort = mapper.toShortDto(eventModel, categoryDto, userDto);
                    eventShort.setRating(analyzerClient.getInteractionsCount(List.of(eventModel.getId()))
                            .map(RecommendedEventProto::getScore)
                            .findFirst()
                            .orElse(0.0));
                    return eventShort;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public EventFullDto getEventById(Long eventId, HttpServletRequest request, Long userId) {

        log.debug("Получен запрос на получение события по id");
        EventModel event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id= %d не было найдено", eventId)));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException(String.format("Событие с id= %d недоступно, так как не опубликовано", eventId));
        }

        collectorClient.collectUserAction(userId, eventId, "ACTION_VIEW", Instant.now());

        log.debug("Собираем событие для ответа");
        CategoryDto categoryDto = categoryClient.getCategoryById(event.getCategoryId());
        UserShortDto userDto = usersClient.getUserById(event.getInitiatorId());
        LocationDto locationDto = locationClient.getLocation(event.getLocationId());
        EventFullDto result = mapper.toFullDto(event, categoryDto, userDto, locationDto);
        result.setRating(analyzerClient.getInteractionsCount(List.of(event.getId()))
                .map(RecommendedEventProto::getScore)
                .findFirst()
                .orElse(0.0));
        return result;
    }

    public EventFullDto getEventByIdForRequest(Long eventId) {
        log.debug("Получен запрос на получение события по id");
        EventModel event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id= %d не было найдено", eventId)));

        log.debug("Собираем событие для ответа");
        CategoryDto categoryDto = categoryClient.getCategoryById(event.getCategoryId());
        UserShortDto userDto = usersClient.getUserById(event.getInitiatorId());
        LocationDto locationDto = locationClient.getLocation(event.getLocationId());
        EventFullDto result = mapper.toFullDto(event, categoryDto, userDto, locationDto);
        result.setRating(analyzerClient.getInteractionsCount(List.of(event.getId()))
                .map(RecommendedEventProto::getScore)
                .findFirst()
                .orElse(0.0));
        return result;
    }

    @Override
    public boolean checkInitiatorAndEventIds(Long eventId, Long userId) {
        log.info("Проверка для реквеста");
        return repository.existsByIdAndInitiatorId(eventId, userId);
    }

    @Override
    public boolean checkEventsByCategoryId(Long categoryId) {
        log.info("Получение событий для сервиса категорий");
        List<EventModel> events = repository.findAllByCategoryId(categoryId);
        return !events.isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getRecommendation(Long userId, Long max) {
        log.info("Получен запрос на рекомендации для пользователя");
        List<Long> eventIds = analyzerClient.getRecommendationsForUser(userId, max)
                .map(RecommendedEventProto::getEventId)
                .toList();
        List<EventModel> events = repository.findAllByIdIn(eventIds);

        return events.stream()
                .map(eventModel -> {
                    CategoryDto categoryDto = categoryClient.getCategoryById(eventModel.getCategoryId());
                    UserShortDto userDto = usersClient.getUserById(eventModel.getInitiatorId());
                    EventShortDto eventShort = mapper.toShortDto(eventModel, categoryDto, userDto);
                    eventShort.setRating(analyzerClient.getInteractionsCount(List.of(eventModel.getId()))
                            .map(RecommendedEventProto::getScore)
                            .findFirst()
                            .orElse(0.0));
                    return eventShort;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public void addLike(Long eventId, Long userId) {
        EventModel event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (requestClient.checkRegistration(eventId, userId)) {
            collectorClient.collectUserAction(userId, eventId, "ACTION_LIKE", Instant.now());
        } else {
            throw new NotFoundException("Пользователь не регистрировался на данное событие");
        }
    }
}

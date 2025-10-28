package ru.yandex.practicum.service.impls;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.errors.exceptions.BadRequestException;
import ru.yandex.practicum.errors.exceptions.ConflictException;
import ru.yandex.practicum.errors.exceptions.DuplicatedDataException;
import ru.yandex.practicum.errors.exceptions.NotFoundException;
import ru.yandex.practicum.event.PublicEventFeignClient;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.enums.EventState;
import ru.yandex.practicum.mapper.RequestMapper;
import ru.yandex.practicum.model.RequestModel;
import ru.yandex.practicum.repository.RequestRepository;
import ru.yandex.practicum.request.dtos.EventRequestStatusUpdateRequestDto;
import ru.yandex.practicum.request.dtos.EventRequestStatusUpdateResultDto;
import ru.yandex.practicum.request.dtos.ParticipationRequestDto;
import ru.yandex.practicum.request.dtos.RequestStatus;
import ru.yandex.practicum.service.RequestService;
import ru.yandex.practicum.users.UsersFeignClient;
import ru.yandex.practicum.users.dtos.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class RequestServiceImpl implements RequestService {
    RequestRepository requestRepository;
    RequestMapper mapper;

    PublicEventFeignClient eventClient;
    UsersFeignClient userClient;

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequests(Long requesterId) {
        validateUserExist(requesterId);

        return requestRepository.findAllByRequesterId(requesterId)
                .stream()
                .sorted(Comparator.comparing(RequestModel::getCreated))
                .map(mapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto createRequest(Long requesterId, Long eventId) {
        return mapper.toParticipationRequestDto(requestRepository.save(validateRequest(requesterId, eventId)));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long requesterId, Long requestId) {
        validateUserExist(requesterId);
        RequestModel participationRequest = validateRequestExist(requesterId, requestId);

        participationRequest.setStatus(RequestStatus.CANCELED);
        return mapper.toParticipationRequestDto(participationRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getCurrentUserEventRequests(Long initiatorId, Long eventId) {
        validateUserExist(initiatorId);
        validateEventExist(eventId);

        if (!eventClient.checkInitiatorAndEventIds(eventId, initiatorId))
            throw new ConflictException(String.format("Событие с id= %d " +
                    "с инициатором id= %d не найдено", eventId, initiatorId));
        return requestRepository.findByEventId(eventId).stream()
                .sorted(Comparator.comparing(RequestModel::getCreated))
                .map(mapper::toParticipationRequestDto).toList();
    }

    @Override
    public EventRequestStatusUpdateResultDto updateParticipationRequestsStatus(Long initiatorId, Long eventId,
                                                                               EventRequestStatusUpdateRequestDto e) {
        log.info("Начало обновления статусов запроса на участие для инициатора ID: {}, события ID: {}",
                initiatorId, eventId);
        validateUserExist(initiatorId);
        EventFullDto event = validateEventExist(eventId);

        if (!event.getInitiator().getId().equals(initiatorId)) {
            log.error("Попытка изменить статус не инициатором события. Инициатор: {}, Запрос: {}",
                    event.getInitiator().getId(), initiatorId);
            throw new ConflictException("Только инициатор события может менять статус запроса на участие в событии");
        }

        long limit = event.getParticipantLimit();
        log.info("Лимит: {}", limit);

        EventRequestStatusUpdateResultDto result = new EventRequestStatusUpdateResultDto();

        if (!event.getRequestModeration() || limit == 0) {
            log.info("Запросы на участие не требуют модерации или лимит участников равен 0.");
            return result;
        }

        List<Long> requestIds = e.getRequestIds();
        RequestStatus status = e.getStatus();

        if (!RequestStatus.REJECTED.equals(status) && !RequestStatus.CONFIRMED.equals(status)) {
            log.error("Недопустимый статус запроса: {}", status);
            throw new BadRequestException("Статус должен быть REJECTED или CONFIRMED");
        }

        if (requestRepository.countByIdInAndEventId(requestIds, eventId) != requestIds.size()) {
            log.error("Некоторые запросы не соответствуют событию с ID: {}", eventId);
            throw new ConflictException(String.format("Не все запросы соответствуют событию с id= %d", eventId));
        }

        if (requestRepository
                .countByEventIdAndStatusEquals(eventId, RequestStatus.CONFIRMED) >= limit) {
            log.error("Достигнут лимит заявок на событие с ID: {}", eventId);
            throw new ConflictException(String.format("Уже достигнут лимит предела заявок на событие с id= %d",
                    eventId));
        }

        LinkedHashMap<Long, RequestModel> requestsMap = requestRepository.findAllByIdIn(requestIds)
                .stream()
                .sorted(Comparator.comparing(RequestModel::getCreated))
                .collect(Collectors.toMap(
                        RequestModel::getId,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        if (requestsMap.values().stream().anyMatch(request -> request.getStatus() != RequestStatus.PENDING)) {
            log.error("Некоторые запросы имеют статус, отличный от PENDING");
            throw new ConflictException("У всех запросов должен быть статус: PENDING");
        }

        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

        long confirmedCount = limit -
                requestRepository.countByEventIdAndStatusEquals(eventId, RequestStatus.CONFIRMED);

        requestsMap.values().forEach(request -> {
            if (status == RequestStatus.REJECTED) {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(mapper.toParticipationRequestDto(request));
                log.info("Заявка ID {} отклонена", request.getId());
            } else {
                if (confirmedRequests.size() < confirmedCount) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(mapper.toParticipationRequestDto(request));
                    log.info("Заявка ID {} подтверждена", request.getId());
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(mapper.toParticipationRequestDto(request));
                    log.info("Заявка ID {} отклонена из-за превышения лимита", request.getId());
                }
            }
        });

        result.getConfirmedRequests().addAll(confirmedRequests);
        result.getRejectedRequests().addAll(rejectedRequests);

        log.info("Сохранение статусов запросов");

        requestsMap.values().forEach(request ->
                log.info("Request ID: {} New Status: {}", request.getId(), request.getStatus())
        );

        requestRepository.saveAll(requestsMap.values());

        event.setConfirmedRequests(requestRepository.countByEventIdAndStatusEquals(eventId, RequestStatus.CONFIRMED));
        log.info("Обновлено ConfirmedRequests для события ID: {}. Новое значение: {}", eventId,
                event.getConfirmedRequests());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getConfirmedRequests(Long eventId) {
        log.info("Получение подтвержденных запросов на участие в сервисе");
        return requestRepository.countConfirmedRequestsByEventIds(eventId, RequestStatus.CONFIRMED);
    }

    private RequestModel validateRequest(Long requesterId, Long eventId) {
        validateUserExist(requesterId);
        EventFullDto event = validateEventExist(eventId);

        validateNotExistsByEventIdAndRequesterId(eventId, requesterId);
        if (event.getInitiator().getId().equals(requesterId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        long limit = event.getParticipantLimit();

        if (limit > 0 &&
                requestRepository.countByEventIdAndStatusEquals(eventId, RequestStatus.CONFIRMED) >= limit) {
            throw new ConflictException("Достигнут лимит запросов на участие");
        }

        RequestModel participationRequest = new RequestModel();
        participationRequest.setRequesterId(requesterId);
        participationRequest.setEventId(eventId);
        participationRequest.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            participationRequest.setStatus(RequestStatus.PENDING);
        }
        return participationRequest;
    }

    private UserShortDto validateUserExist(Long userId) {
        return userClient.getUserById(userId);
    }

    private EventFullDto validateEventExist(Long eventId) {
        return eventClient.getEventForRequestService(eventId);
    }

    private void validateNotExistsByEventIdAndRequesterId(Long eventId, Long requesterId) {
        requestRepository.findByEventIdAndRequesterId(eventId, requesterId)
                .ifPresent(request -> {
                    throw new DuplicatedDataException("Нельзя добавить повторный запрос для этого события");
                });
    }

    private RequestModel validateRequestExist(Long requesterId, Long requestId) {
        RequestModel participationRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос на событие с id= " +
                        "%d не найден.", requestId)));
        if (!participationRequest.getRequesterId().equals(requesterId)) {
            throw new ConflictException(String.format("Данный запрос с id= %d " +
                    "не принадлежит пользователю c id= %d", requestId, requesterId));
        }

        return participationRequest;
    }
}

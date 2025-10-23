package ru.yandex.practicum.service;

import ru.yandex.practicum.request.dtos.EventRequestStatusUpdateRequestDto;
import ru.yandex.practicum.request.dtos.EventRequestStatusUpdateResultDto;
import ru.yandex.practicum.request.dtos.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long requesterId, Long eventId);

    ParticipationRequestDto cancelRequest(Long requesterId, Long requestId);

    List<ParticipationRequestDto> getRequests(Long requesterId);

    List<ParticipationRequestDto> getCurrentUserEventRequests(Long initiatorId, Long eventId);

    EventRequestStatusUpdateResultDto updateParticipationRequestsStatus(Long initiatorId, Long eventId,
                                                                        EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto);

}

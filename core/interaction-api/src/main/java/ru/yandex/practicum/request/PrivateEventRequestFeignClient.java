package ru.yandex.practicum.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.request.dtos.EventRequestStatusUpdateRequestDto;
import ru.yandex.practicum.request.dtos.EventRequestStatusUpdateResultDto;
import ru.yandex.practicum.request.dtos.ParticipationRequestDto;

import java.util.List;

@FeignClient(name = "request-service", contextId = "PrivateEventRequest", path = "/users")
public interface PrivateEventRequestFeignClient {

    @GetMapping("/{userId}/events/{eventId}/requests")
    List<ParticipationRequestDto> getEventRequestsByOwner(@PathVariable @Positive Long userId,
                                                          @PathVariable @Positive Long eventId);

    @PatchMapping("/{userId}/events/{eventId}/requests")
    EventRequestStatusUpdateResultDto updateEventRequest(@PathVariable @Positive Long userId,
                                                         @PathVariable @Positive Long eventId,
                                                         @RequestBody @Valid
                                                         EventRequestStatusUpdateRequestDto update);
}

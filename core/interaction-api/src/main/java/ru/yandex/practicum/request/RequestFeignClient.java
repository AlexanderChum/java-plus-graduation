package ru.yandex.practicum.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.request.dtos.ParticipationRequestDto;

import java.util.List;

@FeignClient(name = "request-service", contextId = "PrivateRequest", path = "/users")
public interface RequestFeignClient {

    @GetMapping("/{userId}/requests")
    List<ParticipationRequestDto> getRequests(@PathVariable @NotNull @Positive Long userId);

    @PostMapping("/{userId}/requests")
    ParticipationRequestDto createRequest(@PathVariable @NotNull @Positive Long userId,
                                          @RequestParam("eventId") @NotNull @Positive Long eventId);

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto cancelRequest(@PathVariable @NotNull @Positive Long userId,
                                          @PathVariable("requestId") @NotNull @Positive Long requestId);
}

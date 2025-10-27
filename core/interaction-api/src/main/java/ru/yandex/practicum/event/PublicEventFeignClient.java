package ru.yandex.practicum.event;

import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.event.dtos.EventFullDto;

@FeignClient(name = "event-service", path = "/events")
public interface PublicEventFeignClient {

    @GetMapping("/{eventId}/requests")
    EventFullDto getEventForRequestService(@PathVariable @Positive Long eventId);

    @GetMapping("{eventId}/request/{userId}")
    boolean checkInitiatorAndEventIds(@PathVariable @Positive Long eventId,
                                      @PathVariable @Positive Long userId);

    @GetMapping("/category/{categoryId}")
    boolean checkEventsByCategoryId(@PathVariable @Positive Long categoryId);
}

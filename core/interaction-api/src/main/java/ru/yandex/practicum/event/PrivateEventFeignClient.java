package ru.yandex.practicum.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.EventShortDto;
import ru.yandex.practicum.event.dtos.NewEventDto;
import ru.yandex.practicum.event.dtos.UpdateEventUserRequest;

import java.util.List;

@FeignClient(name = "event-service", path = "/users")
public interface PrivateEventFeignClient {

    @GetMapping("/{userId}/events")
    List<EventShortDto> getUserEvents(@PathVariable @Positive Long userId,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                      @RequestParam(defaultValue = "10") @Positive Integer size);

    @PostMapping("/{userId}/events")
    EventFullDto createEvent(@Valid @RequestBody NewEventDto newEventDto,
                             @PathVariable @Positive Long userId);

    @GetMapping("/{userId}/events/{eventId}")
    EventFullDto getEventByEventIdAndUserId(@PathVariable @Positive Long userId,
                                            @PathVariable @Positive Long eventId);

    @PatchMapping("/{userId}/events/{eventId}")
    EventFullDto updateEventByEventId(@PathVariable @Positive Long userId,
                                      @PathVariable @Positive Long eventId,
                                      @Valid @RequestBody UpdateEventUserRequest updateEventDto);
}

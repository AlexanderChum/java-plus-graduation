package ru.yandex.practicum.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

import static stat.constant.Const.DATE_TIME_FORMAT;

@FeignClient(name = "event-service", path = "/admin/events")
public interface AdminEventFeignClient {

    @PatchMapping("/{eventId}")
    EventFullDto updateEvent(@Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                             @PathVariable @Positive Long eventId);

    @GetMapping
    List<EventFullDto> getEventsWithAdminFilters(@RequestParam(required = false) List<Long> users,
                                                 @RequestParam(required = false) List<String> states,
                                                 @RequestParam(required = false) List<Long> categories,

                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                                 LocalDateTime rangeStart,

                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                                 LocalDateTime rangeEnd,

                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size);

}

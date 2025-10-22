package ru.yandex.practicum.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.event.dtos.EventFullDto;
import ru.yandex.practicum.event.dtos.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static stat.constant.Const.DATE_TIME_FORMAT;

@FeignClient(name = "event-service", path = "/events")
public interface PublicEventFeignClient {

    @GetMapping
    List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                  @RequestParam(required = false) List<Long> categories,
                                  @RequestParam(required = false) Boolean paid,

                                  @RequestParam(required = false)
                                  @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                  LocalDateTime rangeStart,

                                  @RequestParam(required = false)
                                  @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                  LocalDateTime rangeEnd,

                                  @RequestParam(defaultValue = "false") Boolean onlyAvailable,

                                  @RequestParam(required = false)
                                  @Pattern(regexp = "EVENT_DATE|VIEWS")
                                  String sort,

                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  HttpServletRequest request);

    @GetMapping("/{eventId}")
    EventFullDto getEventById(@PathVariable @Positive Long eventId,
                              HttpServletRequest request);
}

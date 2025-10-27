package ru.yandex.practicum.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "request-service", path = "/users/requests")
public interface RequestFeignClient {

    @GetMapping("/confirmed")
    Long getConfirmedRequests(@RequestParam Long eventId);
}

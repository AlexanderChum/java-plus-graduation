package ru.yandex.practicum.location;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.location.dtos.LocationDto;

@FeignClient(name = "location-service", path = "/categories")
public interface LocationFeignClient {

    @PostMapping
    LocationDto createLocation(@RequestBody LocationDto dto);

    @GetMapping
    LocationDto getLocation(@RequestParam Long locationId);
}

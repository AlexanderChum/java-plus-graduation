package interactionApi.location;

import main.server.location.LocationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "location-service", path = "/categories")
public interface LocationFeignClient {

    @PostMapping
    LocationDto createLocation(@RequestBody LocationDto dto);

    @PostMapping
    LocationDto createLocation(@RequestParam Double lat, @RequestParam Double lon);

    @GetMapping
    LocationDto getLocation(@RequestParam Long locationId);
}

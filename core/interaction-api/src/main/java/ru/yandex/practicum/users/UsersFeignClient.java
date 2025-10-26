package ru.yandex.practicum.users;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.users.dtos.UserShortDto;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UsersFeignClient {

    @GetMapping("/{userId}")
    UserShortDto getUserById(@PathVariable Long userId);
}

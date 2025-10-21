package ru.yandex.practicum.users;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.users.dtos.NewUserDto;
import ru.yandex.practicum.users.dtos.UserDto;

import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UsersFeignClient {

    @GetMapping
    List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> userIds,
                           @PositiveOrZero
                           @RequestParam(name = "from", defaultValue = "0")
                           Integer from,
                           @Positive
                           @RequestParam(name = "size", defaultValue = "10")
                           Integer size);

    @PostMapping
    UserDto createUser(@RequestBody @Valid NewUserDto userDto);

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Long userId);

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable Long userId);
}

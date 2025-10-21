package ru.yandex.practicum.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.users.UsersFeignClient;
import ru.yandex.practicum.users.dtos.NewUserDto;
import ru.yandex.practicum.users.dtos.UserDto;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserController implements UsersFeignClient {
    UserService service;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> userIds,
                                  @PositiveOrZero
                                  @RequestParam(name = "from", defaultValue = "0")
                                  Integer from,
                                  @Positive
                                  @RequestParam(name = "size", defaultValue = "10")
                                  Integer size) {
        log.info("Получен запрос на получение пользователей");
        return service.getUsers(userIds, from, size);
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(NewUserDto userDto) {
        log.info("Получен запрос на создание пользователя");
        return service.createUser(userDto);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(Long userId) {
        log.info("Получен запрос на удаление пользователя");
        service.deleteUser(userId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(Long userId) {
        log.info("Запрос на получение пользователя по id");
        return service.getUserById(userId);
    }
}

package ru.yandex.practicum.service;

import ru.yandex.practicum.users.dtos.NewUserDto;
import ru.yandex.practicum.users.dtos.UserDto;
import ru.yandex.practicum.users.dtos.UserShortDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size);

    UserDto createUser(NewUserDto userDto);

    void deleteUser(Long userId);

    UserShortDto getUserById(Long userId);
}

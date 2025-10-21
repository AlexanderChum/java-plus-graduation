package ru.yandex.practicum.service;

import ru.yandex.practicum.users.dtos.NewUserDto;
import ru.yandex.practicum.users.dtos.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size);

    UserDto createUser(NewUserDto userDto);

    void deleteUser(Long userId);

    UserDto getUserById(Long userId);
}

package main.server.user.service;

import ru.yandex.practicum.users.dtos.NewUserDto;
import main.server.user.dto.UpdateUserDto;
import ru.yandex.practicum.users.dtos.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserDto newUserDto);

    void deleteUserById(Long userId);

    List<UserDto> getUsers(List<Long> userIds, int from, int size);

    UserDto updateUser(Long userId, UpdateUserDto updateUserDto);


}
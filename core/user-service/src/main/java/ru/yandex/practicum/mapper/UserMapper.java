package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.users.dtos.NewUserDto;
import ru.yandex.practicum.users.dtos.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(NewUserDto newUserDto);

    UserDto toUserDto(User user);

    default Page<UserDto> toUserDtoPage(List<User> users, Pageable pageable) {
        List<UserDto> userDtos = users == null ? List.of() : users.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
        return new PageImpl<>(userDtos, pageable, userDtos.size());
    }
}

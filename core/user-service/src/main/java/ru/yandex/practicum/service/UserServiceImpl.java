package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.errors.exceptions.DuplicatedDataException;
import ru.yandex.practicum.errors.exceptions.NotFoundException;
import ru.yandex.practicum.mapper.UserMapper;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.UserRepository;
import ru.yandex.practicum.users.dtos.NewUserDto;
import ru.yandex.practicum.users.dtos.UserDto;
import ru.yandex.practicum.users.dtos.UserShortDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class UserServiceImpl implements UserService {
    UserMapper mapper;
    UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> usersId, Integer from, Integer size) {
        log.info("Запрос в сервис на получение пользователей");
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        Page<User> usersPage;
        if (usersId == null || usersId.isEmpty()) {
            usersPage = repository.findAll(pageable);
        } else {
            usersPage = repository.findByIdIn(usersId, pageable);
        }
        log.debug("Получен список из {} пользователей", usersPage.stream().count());
        return mapper.toUserDtoPage(usersPage.getContent(), pageable).getContent();
    }

    @Override
    public UserDto createUser(NewUserDto userDto) {
        log.info("Запрос в сервис на создание пользователя");
        validateEmailExist(userDto.getEmail());
        User user = repository.save(mapper.toUser(userDto));
        log.debug("Создан пользователь с id = {}", user.getId());
        return mapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Запрос в сервис на удаление пользователя");
        validateUserExist(userId);
        repository.deleteById(userId);
    }

    @Override
    public UserShortDto getUserById(Long userId) {
        log.info("Запрос в сервис на получение пользователя по id");
        return mapper.toUserShortDto(validateUserExist(userId));
    }

    private void validateEmailExist(String email) {
        if (repository.existsByEmail(email)) {
            throw new DuplicatedDataException(String.format("Email - %s уже используется", email));
        }
    }

    private User validateUserExist(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id= %d не найден.", userId)));
    }
}

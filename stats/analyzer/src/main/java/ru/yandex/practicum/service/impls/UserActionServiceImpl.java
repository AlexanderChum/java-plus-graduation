package ru.yandex.practicum.service.impls;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.mapper.UserActionMapper;
import ru.yandex.practicum.repositories.UserActionRepository;
import ru.yandex.practicum.service.UserActionService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class UserActionServiceImpl implements UserActionService {
    UserActionMapper mapper;
    UserActionRepository repository;

    @Override
    public void saveUserAction(UserActionAvro request) {
        repository.save(mapper.toEntity(request));
    }
}

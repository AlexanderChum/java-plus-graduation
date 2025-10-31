package ru.yandex.practicum.service;

import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.models.UserAction;

import java.util.List;

public interface UserActionService {

    void saveUserAction(UserActionAvro request);

    List<UserAction> getMaxWeighted(List<Long> eventIds);

    List<UserAction> getByUser(Long userId);
}

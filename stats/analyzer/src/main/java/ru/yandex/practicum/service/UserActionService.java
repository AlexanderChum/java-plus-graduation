package ru.yandex.practicum.service;

import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionService {

    void saveUserAction(UserActionAvro request);
}

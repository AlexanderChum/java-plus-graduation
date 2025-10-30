package ru.yandex.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.models.UserAction;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
}

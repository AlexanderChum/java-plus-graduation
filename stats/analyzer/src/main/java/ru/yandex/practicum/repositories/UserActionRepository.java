package ru.yandex.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.models.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    List<UserAction> findAllByUserId(Long userId);

    List<UserAction> findByEventIdIn(List<Long> eventIds);
}

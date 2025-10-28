package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}

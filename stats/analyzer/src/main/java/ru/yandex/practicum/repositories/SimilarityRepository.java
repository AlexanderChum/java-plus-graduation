package ru.yandex.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.models.Similarity;

import java.util.List;

public interface SimilarityRepository extends JpaRepository<Similarity, Long> {

    List<Similarity> findByEventA(Long eventId);
    List<Similarity> findByEventB(Long eventId);
    List<Similarity> findByEventAIn(List<Long> eventIds);
    List<Similarity> findByEventBIn(List<Long> eventIds);
}

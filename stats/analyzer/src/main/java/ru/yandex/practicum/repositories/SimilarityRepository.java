package ru.yandex.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.models.Similarity;

public interface SimilarityRepository extends JpaRepository<Similarity, Long> {
}

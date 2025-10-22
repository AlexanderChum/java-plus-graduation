package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.EventModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventModel, Long> {
    boolean existsByIdAndInitiatorId(Long eventId, Long initiatorId);

    List<EventModel> findAllByCategoryId(Long catId);

    Page<EventModel> findByInitiatorId(Long userId, PageRequest eventDate);

    Optional<EventModel> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("""
                SELECT e
                FROM EventModel AS e
                WHERE e.state = 'PUBLISHED'
                AND (:text IS NULL OR e.annotation ILIKE %:text% OR e.description ILIKE %:text%)
                AND (:categories IS NULL OR e.category.id IN :categories)
                AND (:paid IS NULL OR e.paid = :paid)
                AND (CAST(:rangeStart AS timestamp) IS NULL AND e.eventDate >= CURRENT_TIMESTAMP OR e.eventDate >=
                :rangeStart)
                AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate < :rangeEnd)
                AND (:onlyAvailable = false OR e.participantLimit = 0 OR e.participantLimit < e.confirmedRequests)
            """)
    List<EventModel> findAllByFiltersPublic(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable);

    @Query("""
                SELECT e
                FROM EventModel AS e
                WHERE (:users IS NULL OR e.initiator.id IN :users)
                AND (:states IS NULL OR e.state IN :states)
                AND (:categories IS NULL OR e.category.id IN :categories)
                AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart)
                AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate < :rangeEnd)
            """)
    List<EventModel> findAllByFiltersAdmin(
            @Param("users") List<Long> users,
            @Param("states") List<String> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);
}

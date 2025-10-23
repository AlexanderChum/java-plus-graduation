package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.RequestModel;
import ru.yandex.practicum.request.dtos.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<RequestModel, Long> {
    Optional<RequestModel> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    long countByEventIdAndStatusEquals(Long eventId, RequestStatus requestStatus);

    List<RequestModel> findAllByRequesterId(Long requesterId);

    List<RequestModel> findByEventId(Long eventId);

    long countByIdInAndEventId(List<Long> requestIds, Long eventId);

    List<RequestModel> findAllByIdIn(List<Long> requestIds);

    @Query("SELECT r.event.id, COUNT(r)" +
            " FROM ParticipationRequest r" +
            " WHERE r.event.id IN :eventIds AND r.status = :status" +
            " GROUP BY r.event.id")
    List<Object[]> countConfirmedRequestsByEventIds(@Param("eventIds") List<Long> eventIds,
                                                    @Param("status") RequestStatus status);

}

package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import ru.yandex.practicum.request.dtos.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "participation_request")
public class RequestModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @CreatedDate
    @Column(name = "created", updatable = false)
    @NotNull(message = "Дата создания не может быть пустой")
    LocalDateTime created;

    @Column(name = "event_id")
    @NotNull(message = "Event не может быть пустым")
    Long eventId;

    @Column(name = "requester_id")
    @NotNull(message = "Requester не может быть пустым")
    Long requesterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull(message = "Статус не может быть пустым")
    RequestStatus status;
}


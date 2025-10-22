package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Formula;
import ru.yandex.practicum.event.enums.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "event")
public class EventModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Size(max = 2000)
    @Column(name = "annotation")
    String annotation;

    @NotNull(message = "Category не должна быть пустой")
    Long categoryId;

    @Formula("(select count(*) from participation_request p " +
            " where p.event_id = id and p.status = 'CONFIRMED')")
    Long confirmedRequests;

    @Column(name = "created_on")
    @NotNull(message = "СreatedOn не должна быть пустой")
    LocalDateTime createdOn;

    @Size(max = 7000)
    @Column(name = "description")
    String description;

    @NotNull(message = "EventDate не должна быть пустой")
    @Column(name = "event_date")
    LocalDateTime eventDate;

    @NotNull(message = "Initiator не должен быть пустым")
    Long initiatorId;

    @NotNull(message = "Paid не должен быть пустым")
    @Column(name = "paid")
    Boolean paid;

    @Column(name = "participant_limit")
    Long participantLimit;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @NotNull(message = "RequestModeration не должна быть пустой")
    @Column(name = "request_moderation")
    Boolean requestModeration;

    @NotNull(message = "State не должен быть пустым")
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    EventState state;

    @Size(max = 120)
    @NotBlank(message = "Title cannot be blank")
    @Column(name = "title")
    String title;

    @NotNull(message = "Location не должна быть пустой")
    Long locationId;
}

package ru.yandex.practicum.event.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.category.dtos.CategoryDto;
import ru.yandex.practicum.event.enums.EventState;
import ru.yandex.practicum.users.dtos.UserShortDto;
import ru.yandex.practicum.location.dtos.LocationDto;

import java.time.LocalDateTime;

import static ru.practicum.Constants.DATE_TIME_FORMAT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String annotation;

    @JsonProperty("category")
    CategoryDto categoryDto;

    Long confirmedRequests;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime createdOn;

    String description;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime eventDate;

    UserShortDto initiator;

    @JsonProperty("location")
    LocationDto locationDto;

    Boolean paid;
    Long participantLimit;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    Double rating;
}

package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.RequestModel;
import ru.yandex.practicum.request.dtos.ParticipationRequestDto;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    ParticipationRequestDto toParticipationRequestDto(RequestModel participationRequest);
}

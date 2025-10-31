package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.models.Similarity;

@Mapper(componentModel = "spring")
public interface SimilarityMapper {

    @Mapping(target = "id", ignore = true)
    Similarity toEntity(EventSimilarityAvro avro);
}

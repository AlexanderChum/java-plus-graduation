package ru.yandex.practicum.service;

import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;

public interface AggregationService {
    List<EventSimilarityAvro> calculateSimilarity(UserActionAvro request);
}

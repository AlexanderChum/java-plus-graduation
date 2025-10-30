package ru.yandex.practicum.service;

import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface SimilarityService {

    void saveSimilarity(EventSimilarityAvro request);
}

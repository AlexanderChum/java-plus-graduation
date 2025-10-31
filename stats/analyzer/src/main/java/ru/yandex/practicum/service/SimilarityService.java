package ru.yandex.practicum.service;

import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.models.Similarity;

import java.util.List;

public interface SimilarityService {

    void saveSimilarity(EventSimilarityAvro request);

    List<Similarity> getSimilarToEvent(Long eventId);

    List<Similarity> getSimilarToEvents(List<Long> eventIds);
}

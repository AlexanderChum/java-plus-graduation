package ru.yandex.practicum.service.impls;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.mapper.SimilarityMapper;
import ru.yandex.practicum.models.Similarity;
import ru.yandex.practicum.repositories.SimilarityRepository;
import ru.yandex.practicum.service.SimilarityService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class SimilarityServiceImpl implements SimilarityService {
    SimilarityRepository repository;
    SimilarityMapper mapper;

    @Override
    public void saveSimilarity(EventSimilarityAvro request) {
        repository.save(mapper.toEntity(request));
    }

    @Override
    public List<Similarity> getSimilarToEvent(Long eventId) {
        List<Similarity> similarities = findByEventAIdOrEventBId(eventId);

        return similarities.stream()
                .map(s -> {
                    if (eventId.equals(s.getEventA())) {
                        return s;
                    } else {
                        return swapEvents(s);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Similarity> getSimilarToEvents(List<Long> eventIds) {
        List<Similarity> similarities = findByEventAIdInOrEventBIdIn(eventIds);

        return similarities.stream()
                .map(s -> {
                    if (eventIds.contains(s.getEventA())) {
                        return s;
                    } else {
                        return swapEvents(s);
                    }
                })
                .collect(Collectors.toList());
    }

    private List<Similarity> findByEventAIdOrEventBId(Long eventId) {
        List<Similarity> result = new ArrayList<>();
        result.addAll(repository.findByEventA(eventId));
        result.addAll(repository.findByEventB(eventId));
        return result;
    }

    private List<Similarity> findByEventAIdInOrEventBIdIn(List<Long> eventIds) {
        List<Similarity> result = new ArrayList<>();
        result.addAll(repository.findByEventAIn(eventIds));
        result.addAll(repository.findByEventBIn(eventIds));
        return result;
    }

    private Similarity swapEvents(Similarity similarity) {
        return new Similarity(
                similarity.getId(),
                similarity.getEventB(),
                similarity.getEventA(),
                similarity.getScore(),
                similarity.getTimestamp()
        );
    }
}

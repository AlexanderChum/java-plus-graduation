package ru.yandex.practicum.service.impls;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.mapper.SimilarityMapper;
import ru.yandex.practicum.repositories.SimilarityRepository;
import ru.yandex.practicum.service.SimilarityService;

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
}

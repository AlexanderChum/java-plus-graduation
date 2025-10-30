package ru.yandex.practicum.service.impls;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.stats.eventPredictions.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.eventPredictions.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.eventPredictions.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.eventPredictions.UserPredictionsRequestProto;
import ru.yandex.practicum.service.RecommendationsService;
import ru.yandex.practicum.service.SimilarityService;
import ru.yandex.practicum.service.UserActionService;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class RecommendationServiceImpl implements RecommendationsService {
    UserActionService userActionService;
    SimilarityService similarityService;

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {

    }


    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {

    }


    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {

    }
}

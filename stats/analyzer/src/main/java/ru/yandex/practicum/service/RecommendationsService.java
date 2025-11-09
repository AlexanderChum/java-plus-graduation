package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.stats.eventPredictions.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.eventPredictions.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.eventPredictions.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.eventPredictions.UserPredictionsRequestProto;

import java.util.List;

public interface RecommendationsService {

    List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request);

    List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request);

    List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request);
}

package ru.yandex.practicum.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.stats.eventPredictions.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.eventPredictions.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.eventPredictions.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.eventPredictions.UserPredictionsRequestProto;
import ru.yandex.practicum.service.RecommendationsService;
import ru.yandex.practicum.stats.service.dashboard.RecommendationsControllerGrpc;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    RecommendationsService service;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> response) {
        log.info("Запрос на получение пользовательских рекомендаций");
        try {
            List<RecommendedEventProto> events = service.getRecommendationsForUser(request);
            events.forEach(response::onNext);
            response.onCompleted();
        } catch (Exception e) {
            response.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request, StreamObserver<RecommendedEventProto> response) {
        log.info("Запрос на получение похожих событий");
        try {
            List<RecommendedEventProto> events = service.getSimilarEvents(request);
            events.forEach(response::onNext);
            response.onCompleted();
        } catch (Exception e) {
            response.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> response) {
        log.info("Запрос на получение количества взаимодействий");
        try {
            List<RecommendedEventProto> events = service.getInteractionsCount(request);
            events.forEach(response::onNext);
            response.onCompleted();
        } catch (Exception e) {
            response.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}

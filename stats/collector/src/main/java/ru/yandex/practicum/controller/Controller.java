package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.message.action.UserActionProto;
import ru.yandex.practicum.grpc.message.collector.UserActionControllerGrpc;
import ru.yandex.practicum.service.CollectorService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Controller extends UserActionControllerGrpc.UserActionControllerImplBase {
    CollectorService service;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> response) {
        log.info("Получение действия из grpc");
        try {
            service.createUserAction(request);
            response.onNext(Empty.getDefaultInstance());
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

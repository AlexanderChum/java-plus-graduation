package client;

import com.google.protobuf.Timestamp;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.stats.action.ActionTypeProto;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;
import ru.yandex.practicum.grpc.stats.collector.UserActionControllerGrpc;

import java.time.Instant;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollectorClient {

    @GrpcClient("collector")
    UserActionControllerGrpc.UserActionControllerBlockingStub server;

    public void collectUserAction(Long userId, Long eventId, String actionType, Instant timestamp) {
        UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.valueOf(actionType))
                .setTimestamp(buildTimestamp(timestamp))
                .build();
        server.collectUserAction(request);
    }

    private Timestamp buildTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}

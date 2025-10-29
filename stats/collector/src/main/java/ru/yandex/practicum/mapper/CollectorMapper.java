package ru.yandex.practicum.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import ru.yandex.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.grpc.message.action.ActionTypeProto;
import ru.yandex.practicum.grpc.message.action.UserActionProto;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface CollectorMapper {
    UserActionAvro mapToAvro(UserActionProto action);

    default ActionTypeAvro mapActionType(ActionTypeProto actionType) {
        if (actionType == null) {
            return null;
        }
        switch (actionType) {
            case ACTION_VIEW: return ActionTypeAvro.VIEW;
            case ACTION_REGISTER: return ActionTypeAvro.REGISTER;
            case ACTION_LIKE: return ActionTypeAvro.LIKE;
            default: throw new IllegalArgumentException("Неизвестный тип действия: " + actionType);
        }
    }

    default Instant mapTimestamp(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
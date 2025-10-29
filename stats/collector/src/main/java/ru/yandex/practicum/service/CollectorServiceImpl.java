package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.grpc.message.action.UserActionProto;
import ru.yandex.practicum.kafkaProducer.CollectorProducerConfig;
import ru.yandex.practicum.mapper.CollectorMapper;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CollectorServiceImpl implements CollectorService{
    KafkaTemplate<String, Object> kafkaTemplate;
    CollectorProducerConfig config;
    CollectorMapper mapper;

    @Override
    public void createUserAction(UserActionProto request) {
        UserActionAvro avro = mapper.mapToAvro(request);
        kafkaTemplate.send(config.actionTopic, avro)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Действие успешно отправлено");
                    } else {
                        log.error("Не удалось отправить действие");
                    }
                });
    }
}

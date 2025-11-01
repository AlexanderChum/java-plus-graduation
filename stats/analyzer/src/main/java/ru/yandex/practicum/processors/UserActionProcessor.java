package ru.yandex.practicum.processors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.kafka.ConsumerFactory;
import ru.yandex.practicum.service.UserActionService;

import java.time.Duration;
import java.util.List;

import static ru.practicum.Constants.POLL_INTERVAL;
import static ru.practicum.Constants.USER_ACTION_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserActionProcessor implements Runnable {
    UserActionService service;
    ConsumerFactory factory;

    @Override
    public void run() {
        KafkaConsumer<String, SpecificRecordBase> consumer = factory.createUserActionConsumer();

        try {
            consumer.subscribe(List.of(USER_ACTION_TOPIC));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(POLL_INTERVAL));
                log.info("Получено {} записей из топика {}", records.count(), USER_ACTION_TOPIC);

                records.forEach(record -> {
                    try {
                        UserActionAvro request = (UserActionAvro) record.value();
                        service.saveUserAction(request);
                    } catch (Exception e) {
                        log.error("Ошибка обработки записи", e);
                    }
                });

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            log.error("Ошибка WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }
}

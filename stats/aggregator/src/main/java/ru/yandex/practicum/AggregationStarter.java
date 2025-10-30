package ru.yandex.practicum;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.kafkaFactories.AggregatorConsumer;
import ru.yandex.practicum.kafkaFactories.AggregatorProducer;
import ru.yandex.practicum.service.AggregationService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregationStarter {
    AggregatorConsumer consumerFactory;
    AggregatorProducer producerFactory;
    AggregationService service;

    public void start() {
        KafkaConsumer<String, SpecificRecordBase> consumer = consumerFactory.createConsumer();
        KafkaProducer<String, SpecificRecordBase> producer = producerFactory.createProducer();

        try {
            log.info("Попытка подписаться на топик действий");
            consumer.subscribe(List.of(consumerFactory.actionTopic));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(1));

                records.forEach(record -> {
                    try {
                        UserActionAvro request = (UserActionAvro) record.value();
                        service.calculateSimilarity(request).forEach(result -> {
                            producer.send(new ProducerRecord<>(producerFactory.similarityTopic, result));
                        });
                    } catch (Exception e) {
                        log.error("Ошибка обработки записи", e);
                    }
                });

                consumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.error("Ошибка WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}

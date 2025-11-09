package ru.yandex.practicum.kafkaFactories;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static ru.practicum.Constants.VALUE_SERIALIZER;
import static ru.practicum.Constants.BOOTSTRAP_SERVER;
import static ru.practicum.Constants.KEY_SERIALIZER;

@Component
@Slf4j
@FieldDefaults(makeFinal = true)
public class AggregatorProducer {

    @Bean
    public KafkaProducer<String, SpecificRecordBase> createProducer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KEY_SERIALIZER);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER);

        log.info("Создание продюсера для агрегатора");
        return new KafkaProducer<>(configProps);
    }
}

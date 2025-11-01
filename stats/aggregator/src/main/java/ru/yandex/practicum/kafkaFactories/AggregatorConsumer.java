package ru.yandex.practicum.kafkaFactories;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static ru.practicum.Constants.AGGREGATOR_DESERIALIZER;
import static ru.practicum.Constants.AGGREGATOR_GROUP;
import static ru.practicum.Constants.BOOTSTRAP_SERVER;
import static ru.practicum.Constants.KEY_DESERIALIZER;

@Configuration
@Slf4j
@FieldDefaults(makeFinal = true)
public class AggregatorConsumer {

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> createConsumer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KEY_DESERIALIZER);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AGGREGATOR_DESERIALIZER);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, AGGREGATOR_GROUP);

        log.info("Создание консьюмера для аггрегатора");
        return new KafkaConsumer<>(configProps);
    }
}
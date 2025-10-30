package ru.yandex.practicum.kafkaFactories;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class AggregatorConsumer {
    public final String bootstrapServers = "localhost:9092";
    public final String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
    public final String valueDeserializer = "ru.yandex.practicum.deserializers.UserActionDeserializer";
    public final String clientGroup = "aggregator-group";
    public final String actionTopic = "stats.user-actions.v1";


    @Bean
    public KafkaConsumer<String, SpecificRecordBase> createConsumer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, clientGroup);

        log.info("Создание консьюмера для аггрегатора");
        return new KafkaConsumer<>(configProps);
    }
}
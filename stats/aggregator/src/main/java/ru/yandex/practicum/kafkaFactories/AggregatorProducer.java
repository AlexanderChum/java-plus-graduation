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

@Component
@Slf4j
@FieldDefaults(makeFinal = true)
public class AggregatorProducer {
    public String bootstrapServers = "localhost:9092";
    public String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
    public String valueSerializer = "ru.yandex.practicum.serializers.AvroSerializer";
    public String similarityTopic = "stats.events-similarity.v1";

    @Bean
    public KafkaProducer<String, SpecificRecordBase> createProducer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        log.info("Создание продюсера для агрегатора");
        return new KafkaProducer<>(configProps);
    }
}

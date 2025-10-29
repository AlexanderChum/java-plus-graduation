package ru.yandex.practicum.kafkaProducer;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@Getter
@Setter
public class CollectorProducerConfig {
    public final String bootstrapServers = "localhost:9092";
    public final String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
    public final String valueSerializer = "ru.yandex.practicum.serializers.AvroSerializer";
    public final String actionTopic = "stats.user-actions.v1";

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        log.info("Создание ProducerFactory");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        log.info("Создание KafkaTemplate");
        return new KafkaTemplate<>(producerFactory());
    }
}

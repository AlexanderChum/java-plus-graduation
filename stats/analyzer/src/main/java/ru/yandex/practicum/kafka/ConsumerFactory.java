package ru.yandex.practicum.kafka;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static ru.practicum.Constants.ANALYZER_SIMILARITY_DESERIALIZER;
import static ru.practicum.Constants.ANALYZER_SIMILARITY_GROUP;
import static ru.practicum.Constants.ANALYZER_USER_DESERIALIZER;
import static ru.practicum.Constants.ANALYZER_USER_GROUP;
import static ru.practicum.Constants.BOOTSTRAP_SERVER;
import static ru.practicum.Constants.KEY_DESERIALIZER;

@Configuration
@Slf4j
@FieldDefaults(makeFinal = true)
public class ConsumerFactory {

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> createUserActionConsumer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KEY_DESERIALIZER);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ANALYZER_USER_DESERIALIZER);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, ANALYZER_USER_GROUP);

        log.info("Создание консьюмера действий для анализатора");
        return new KafkaConsumer<>(configProps);
    }

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> createSimilarityConsumer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KEY_DESERIALIZER);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ANALYZER_SIMILARITY_DESERIALIZER);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, ANALYZER_SIMILARITY_GROUP);

        log.info("Создание консьюмера схожести для анализатора");
        return new KafkaConsumer<>(configProps);
    }
}

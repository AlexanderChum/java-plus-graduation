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

@Configuration
@Slf4j
@FieldDefaults(makeFinal = true)
public class ConsumerFactory {
    String bootstrapServers = "localhost:9092";
    String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";

    String userActionDeserializer = "ru.yandex.practicum.deserializers.UserActionDeserializer";
    String similarityDeserializer = "ru.yandex.practicum.deserializers.SimilarityDeserializer";

    String clientUserActionGroup = "analyzer-user-action-group";
    String clientSimilaritiesGroup = "analyzer-similarity-group";

    public String userActionTopic = "stats.user-actions.v1";
    public String similaritiesTopic = "stats.events-similarity.v1";

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> createUserActionConsumer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, userActionDeserializer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, clientUserActionGroup);

        log.info("Создание консьюмера действий для анализатора");
        return new KafkaConsumer<>(configProps);
    }

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> createSimilarityConsumer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, similarityDeserializer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, clientSimilaritiesGroup);

        log.info("Создание консьюмера схожести для анализатора");
        return new KafkaConsumer<>(configProps);
    }
}

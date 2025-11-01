package ru.practicum;

public class Constants {

    //Time constants
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_MILLI = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    //Kafka constants
    public static final String BOOTSTRAP_SERVER = "localhost:9092";

    public static final String KEY_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
    public static final String KEY_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";
    public static final String VALUE_SERIALIZER = "ru.yandex.practicum.serializers.AvroSerializer";

    public static final String AGGREGATOR_DESERIALIZER = "ru.yandex.practicum.deserializers.UserActionDeserializer";
    public static final String ANALYZER_USER_DESERIALIZER = "ru.yandex.practicum.deserializers.UserActionDeserializer";
    public static final String ANALYZER_SIMILARITY_DESERIALIZER =
            "ru.yandex.practicum.deserializers.SimilarityDeserializer";

    public static final String USER_ACTION_TOPIC = "stats.user-actions.v1";
    public static final String SIMILARITY_TOPIC = "stats.events-similarity.v1";

    public static final String AGGREGATOR_GROUP = "aggregator-group";
    public static final String ANALYZER_USER_GROUP = "analyzer-user-action-group";
    public static final String ANALYZER_SIMILARITY_GROUP = "analyzer-similarity-group";

    public static final int POLL_INTERVAL = 1;

    //Header constants
    public static final String USER_HEADER = "X-EWM-USER-ID";

}
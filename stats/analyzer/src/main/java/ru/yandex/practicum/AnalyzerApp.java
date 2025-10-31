package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.processors.SimilarityProcessor;
import ru.yandex.practicum.processors.UserActionProcessor;

@SpringBootApplication
@EnableDiscoveryClient
@ConfigurationPropertiesScan
public class AnalyzerApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApp.class, args);

        SimilarityProcessor similarityProcessor = context.getBean(SimilarityProcessor.class);
        UserActionProcessor userActionProcessor = context.getBean(UserActionProcessor.class);

        Thread userActionsThread = new Thread(userActionProcessor);
        userActionsThread.setName("UserActionHandlerThread");
        userActionsThread.start();

        similarityProcessor.run();
    }
}

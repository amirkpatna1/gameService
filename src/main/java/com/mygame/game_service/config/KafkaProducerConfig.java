package com.mygame.game_service.config;

import com.mygame.game_service.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.mygame.game_service.constant.GameConstants.KAFKA_PRODUCER_SERVICE_3;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    @Value("${spring.kafka.producer.client-id}")
    private String clientId;

    @Bean(name = KAFKA_PRODUCER_SERVICE_3)
    public KafkaProducerService getKafkaProducerBean() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        configProps.put(ProducerConfig.ACKS_CONFIG, "-1");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        ProducerFactory<String, Object> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        return new KafkaProducerService(kafkaTemplate);
    }
}

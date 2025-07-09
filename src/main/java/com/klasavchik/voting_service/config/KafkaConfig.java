package com.klasavchik.voting_service.config;

import com.klasavchik.voting_service.dto.UserRequest;
import com.klasavchik.voting_service.dto.VotingCreateRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    private Map<String, Object> commonConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put("value.serializer.encoding", "UTF-8"); // Важная строка
        return props;
    }

    // === Для UserRequest ===

    @Bean
    public ConsumerFactory<String, UserRequest> userConsumerFactory() {
        JsonDeserializer<UserRequest> deserializer = new JsonDeserializer<>(UserRequest.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                commonConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "userKafkaListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UserRequest> userKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userConsumerFactory());
        return factory;
    }

    // === Для VotingCreateRequest ===

    @Bean
    public ConsumerFactory<String, VotingCreateRequest> votingConsumerFactory() {
        JsonDeserializer<VotingCreateRequest> deserializer = new JsonDeserializer<>(VotingCreateRequest.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                commonConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "votingKafkaListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, VotingCreateRequest> votingKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, VotingCreateRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(votingConsumerFactory());
        return factory;
    }
}

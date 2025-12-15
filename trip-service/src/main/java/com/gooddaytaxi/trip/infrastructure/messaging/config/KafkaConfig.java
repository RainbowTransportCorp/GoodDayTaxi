package com.gooddaytaxi.trip.infrastructure.messaging.config;

import com.gooddaytaxi.trip.infrastructure.messaging.model.EventEnvelope;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:trip-service}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, EventEnvelope<?>> tripCreateRequestConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //수동 ack 쓸 거라 auto commit 끔
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // ErrorHandlingDeserializer로 감싸기
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);



        JsonDeserializer<EventEnvelope<?>> valueDeserializer =
                new JsonDeserializer<>(EventEnvelope.class);

        valueDeserializer.addTrustedPackages(
                "com.gooddaytaxi.trip.infrastructure.messaging.model",
                "java.util",
                "java.time"
        );
        valueDeserializer.setUseTypeHeaders(false);
        valueDeserializer.setRemoveTypeHeaders(true);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventEnvelope<?>>
    tripCreateRequestKafkaListenerContainerFactory() {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, EventEnvelope<?>>();

        factory.setConsumerFactory(tripCreateRequestConsumerFactory());

        // ✅ “처리 성공할 때만 커밋”
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // ✅ retry 3회 (총 4번 시도: 최초 + 3회)
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(1000L, 3)));

        return factory;
    }
}

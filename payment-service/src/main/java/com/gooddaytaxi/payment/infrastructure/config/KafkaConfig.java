package com.gooddaytaxi.payment.infrastructure.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@EnableScheduling
@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    //구매자 설정
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        // 프로듀서 팩토리 설정을 위한 맵 생성
        Map<String, Object> configProps = new HashMap<>();
        //Kafka 브로커의 주소 설정
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //메시지 키, 값 직렬화
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 설정된 프로퍼티로 DefaultKafkaProducerFactory 생성하여 반환
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    //소비자 설정
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        // 컨슈머 팩토리 설정을 위한 맵을 생성합니다.
        Map<String, Object> configProps = new HashMap<>();
        // Kafka 브로커의 주소 설정
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //메시지 키, 값 직렬화
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // 컨슈머 그룹 아이디 설정
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // 설정된 프로퍼티로 DefaultKafkaConsumerFactory를 생성하여 반환
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    // Kafka 리스너 컨테이너 팩토리를 생성하는 빈을 정의합니다.
    // ConcurrentKafkaListenerContainerFactory는 Kafka 메시지를 비동기적으로 수신하는 리스너 컨테이너를 생성하는 데 사용됩니다.
    // 이 팩토리는 @KafkaListener 어노테이션이 붙은 메서드들을 실행할 컨테이너를 제공합니다.
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(ConsumerFactory<String, String> cf) {
        // ConcurrentKafkaListenerContainerFactory를 생성합니다.
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        // 컨슈머 팩토리를 리스너 컨테이너 팩토리에 설정합니다.
        factory.setConsumerFactory(cf);
        // 설정된 리스너 컨테이너 팩토리를 반환합니다.
        return factory;
    }
}

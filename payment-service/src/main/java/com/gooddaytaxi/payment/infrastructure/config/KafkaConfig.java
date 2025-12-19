package com.gooddaytaxi.payment.infrastructure.config;

import com.gooddaytaxi.payment.application.event.EventEnvelope;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.backoff.FixedBackOff;

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

    //소비자 객체 설정
    @Bean
    public DefaultKafkaConsumerFactory<String, EventEnvelope> consumerFactory() {
        // 컨슈머 팩토리 설정을 위한 맵을 생성합니다.
        Map<String, Object> props = new HashMap<>();
        // Kafka 브로커의 주소 설정
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // 컨슈머 그룹 아이디 설정
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //메시지 키, 값 역직렬화
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // EventEnvelope 객체로 역직렬화
        JsonDeserializer<EventEnvelope> valueDeserializer = new JsonDeserializer<>(EventEnvelope.class);
        //신뢰할 수 있는 패키지들 설정 -> 해당 역직렬화에 사용되는 파일들의 폴더들
        valueDeserializer.addTrustedPackages(
                "com.gooddaytaxi.payment.application.event",
                "java.util",
                "java.time",
                "java.math"
        );
        //카프카 헤더에 있는 타입정보 사용하지 않도록 설정
        valueDeserializer.setUseTypeHeaders(false);
        //역직렬화 후 헤더에서 타입정보 제거하도록 설정
        valueDeserializer.setRemoveTypeHeaders(true);

        // 설정된 프로퍼티로 DefaultKafkaConsumerFactory를 생성하여 반환
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(valueDeserializer)
        );
    }

    // Kafka 리스너 컨테이너 팩토리를 생성하는 빈을 정의합니다.
    // ConcurrentKafkaListenerContainerFactory는 Kafka 메시지를 비동기적으로 수신하는 리스너 컨테이너를 생성하는 데 사용됩니다.
    // 이 팩토리는 @KafkaListener 어노테이션이 붙은 메서드들을 실행할 컨테이너를 제공합니다.
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventEnvelope<?>> kafkaListenerContainerFactory(ConsumerFactory<String, EventEnvelope> consumerFactory) {
        // ConcurrentKafkaListenerContainerFactory를 생성합니다.
        ConcurrentKafkaListenerContainerFactory<String, EventEnvelope<?>> factory = new ConcurrentKafkaListenerContainerFactory<>();
        // 컨슈머 팩토리를 리스너 컨테이너 팩토리에 설정합니다.
        factory.setConsumerFactory(consumerFactory);

        // 메시지 처리가 완료된 후에만 커밋하도록 수동 ACK 사용
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL);
        // 처리 실패 시 1초 간격으로 3회 재시도
        DefaultErrorHandler errorHandler =new DefaultErrorHandler(new FixedBackOff(1000L, 3));
        //재시도하지 않을 예외 유형들을 지정
        errorHandler.addNotRetryableExceptions(
                DeserializationException.class,   //역직렬화 예외
                MessageConversionException.class,  //kafka 메시지를 @KafkaListener 메서드 파라미터 타입으로 변환하지 못했을 때
                MethodArgumentResolutionException.class  //메서드 인자가 잘못된 경우 예외 - 코드가 잘못됨
        );
        //DLQ를 성공했을때 해당 레코드가 처리된 것으로 간주하고 커밋하도록 설정
        errorHandler.setCommitRecovered(true);
        factory.setCommonErrorHandler(errorHandler);

        // 설정된 리스너 컨테이너 팩토리를 반환합니다.
        return factory;
    }

    /*
    * dlq 설정 + 후에 프로듀서와 통합 고민 필요
    * */
    //DLQ(Dead Letter Queue) 프로듀서 팩토리 정의
    @Bean
    public ProducerFactory<String, Object> dlqProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    //DLQ Kafka 템플릿 정의
    @Bean
    public KafkaTemplate<String, Object> dlqKafkaTemplate() {
        return new KafkaTemplate<>(dlqProducerFactory());
    }

    //재시도까지 다 실패한 레코드를 DLQ 토픽으로 재발행
    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Object> dlqKafkaTemplate) {
        return new DeadLetterPublishingRecoverer(
                dlqKafkaTemplate,
                //실패한 원본 레코드와 예외를 보고 어느 토픽/파티션으로 보낼지 결정  ex) trip.ended -> trip.ended.DLQ
                //단일 파티션이기에 0으로 고정, 다중 파티션으로 확장시 원본 파티션 번호 (record.partition())사용
                (record, ex) -> new TopicPartition(record.topic() + ".DLQ", 0)
        );
    }

}

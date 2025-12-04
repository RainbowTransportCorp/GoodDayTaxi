package com.gooddaytaxi.support.adapter.out.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** RabbitMQ 서버 설정 파일
 * 메시지 브로커 동작 제어 : 사용 포트, 클러스터링, 가상 호스트 설정 등
 * 또 다른 RabbitMQ 설정은 application yaml 파일에 있음
 */
@Slf4j
@EnableRabbit
@Configuration
public class RabbitMQConfig {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    // Exchange
    public static final String EXCHANGE = "gooddaytaxi";
    // Routing Key
    public static final String ROUTING_KEY = "push.send";
    // Queue
    public static final String SYSTEM_QUEUE = "gooddaytaxi.system.queue";
    public static final String DISPATCH_QUEUE = "gooddaytaxi.dispatch.queue";
    public static final String TRIP_QUEUE = "gooddaytaxi.trip.queue";
    public static final String PAYMENT_QUEUE = "gooddaytaxi.payment.queue";

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQConfig(RabbitTemplate rabbitTemplate) { this.rabbitTemplate = rabbitTemplate; }
    /* Exchange
     *
     */
    @Bean
    public TopicExchange exchange() { return new TopicExchange(EXCHANGE); }

    /* Queue
     * Dispatch, Trip, Payment, System에 대한 Queue
     */
    @Bean public Queue queueSystem() { return QueueBuilder.durable(SYSTEM_QUEUE).withArgument("x-max-priority", 10).build(); } // 우선 순위 부여
    @Bean public Queue queueDispatch() { return QueueBuilder.durable(DISPATCH_QUEUE).withArgument("x-max-priority", 10).build(); }
    @Bean public Queue queueTrip() { return QueueBuilder.durable(TRIP_QUEUE).withArgument("x-max-priority", 10).build(); }
    @Bean public Queue queuePayment() { return QueueBuilder.durable(PAYMENT_QUEUE).withArgument("x-max-priority", 10).build(); }

    /* Binding
     * Dispatch, Trip, Payment, System에 대한 Binding
     */
    @Bean public Binding bindingSystem(TopicExchange exchange) { return BindingBuilder.bind(queueSystem()).to(exchange).with(ROUTING_KEY); }
    @Bean public Binding bindingDispatch(TopicExchange exchange) { return BindingBuilder.bind(queueDispatch()).to(exchange).with(ROUTING_KEY); }
    @Bean public Binding bindingTrip(TopicExchange exchange) { return BindingBuilder.bind(queueTrip()).to(exchange).with(ROUTING_KEY); }
    @Bean public Binding bindingPayment(TopicExchange exchange) { return BindingBuilder.bind(queuePayment()).to(exchange).with(ROUTING_KEY); }

    // TODO: err 관련 binding, queue 작성

    /* RabbitTemplate
     *
     */
    @PostConstruct
    public void rabbitTemplateBasedInApplicationYML() {
        // 브로커가 메시지를 정상 수신했는지 여부
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                // 로깅 or 모니터링
                log.info("Message confirmed(successfully delivered). correlation: {}", correlationData);
            } else {
                // 재시도 로직 or 알림
                log.warn("Message NOT confirmed(delivery failed). correlation: {}, cause: {}", correlationData, cause);
            }
        });

        // 라우팅 실패 시 메시지가 되돌아 올 때(publisher-returns:true 일 때만 동작)
        rabbitTemplate.setReturnsCallback(returned -> {
            // DLQ로 보내거나, 모니터링/알림
            log.warn("Message Routing Failed. Returned message: {}, replyText: {}, routingKey: {}",
                    returned.getMessage(), returned.getReplyText(), returned.getRoutingKey());
        });
    }


    /* Listener Container (Consumer 쪽)
     *
     */
    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    // ===============================
    //  JSON Converter + DateTime
    // ===============================

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(dateTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public JavaTimeModule dateTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));
        return module;
    }
}

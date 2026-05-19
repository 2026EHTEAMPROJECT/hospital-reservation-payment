package com.hospital.payment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "hospital.exchange";

    // booking-service -> payment-service
    public static final String PAYMENT_REQUEST_QUEUE = "booking.payment.queue";
    public static final String PAYMENT_REQUEST_ROUTING_KEY = "booking.payment";

    // payment-service -> notification-service
    public static final String PAYMENT_NOTIFICATION_QUEUE = "payment.notification.queue";
    public static final String PAYMENT_NOTIFICATION_ROUTING_KEY = "payment.notification";

    @Bean
    public DirectExchange hospitalExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue paymentRequestQueue() {
        return new Queue(PAYMENT_REQUEST_QUEUE, true);
    }

    @Bean
    public Queue paymentNotificationQueue() {
        return new Queue(PAYMENT_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding paymentRequestBinding(Queue paymentRequestQueue, DirectExchange hospitalExchange) {
        return BindingBuilder.bind(paymentRequestQueue).to(hospitalExchange).with(PAYMENT_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding paymentNotificationBinding(Queue paymentNotificationQueue, DirectExchange hospitalExchange) {
        return BindingBuilder.bind(paymentNotificationQueue).to(hospitalExchange).with(PAYMENT_NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}

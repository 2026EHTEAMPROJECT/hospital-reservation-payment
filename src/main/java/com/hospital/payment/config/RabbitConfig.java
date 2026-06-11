package com.hospital.payment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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
    private static final String PAYMENT_REQUEST_ROUTING_KEY = "booking.payment";

    // booking-service(예약취소) -> payment-service(환불)
    public static final String PAYMENT_REFUND_QUEUE = "payment.refund.queue";
    private static final String PAYMENT_REFUND_ROUTING_KEY = "payment.refund";

    // payment-service -> notification-service
    public static final String PAYMENT_NOTIFICATION_QUEUE = "payment.notification.queue";
    public static final String PAYMENT_NOTIFICATION_ROUTING_KEY = "payment.notification";

    // Dead-letter exchange / queue
    public static final String DLX = "hospital.dlx";
    public static final String PAYMENT_DLQ = "payment.request.dlq";

    @Bean
    public DirectExchange hospitalExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }

    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable(PAYMENT_REQUEST_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", PAYMENT_DLQ)
                .build();
    }

    @Bean
    public Queue paymentDeadLetterQueue() {
        return QueueBuilder.durable(PAYMENT_DLQ).build();
    }

    @Bean
    public Binding paymentDlqBinding(Queue paymentDeadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(paymentDeadLetterQueue).to(deadLetterExchange).with(PAYMENT_DLQ);
    }

    @Bean
    public Queue paymentNotificationQueue() {
        // 라이브 큐(notification-service 가 먼저 선언)가 DLX 인자를 갖고 있어, 인자 없이
        // 선언하면 PRECONDITION_FAILED(406)로 채널이 닫혀 기동에 실패한다. notification 의
        // 동일 큐 선언과 인자를 일치시킨다(x-dead-letter-exchange=hospital.dlx,
        // x-dead-letter-routing-key=payment.notification.dlq).
        return QueueBuilder.durable(PAYMENT_NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", "payment.notification.dlq")
                .build();
    }

    @Bean
    public Binding paymentRequestBinding(Queue paymentRequestQueue, DirectExchange hospitalExchange) {
        return BindingBuilder.bind(paymentRequestQueue).to(hospitalExchange).with(PAYMENT_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Queue paymentRefundQueue() {
        return QueueBuilder.durable(PAYMENT_REFUND_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", PAYMENT_DLQ)
                .build();
    }

    @Bean
    public Binding paymentRefundBinding(Queue paymentRefundQueue, DirectExchange hospitalExchange) {
        return BindingBuilder.bind(paymentRefundQueue).to(hospitalExchange).with(PAYMENT_REFUND_ROUTING_KEY);
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

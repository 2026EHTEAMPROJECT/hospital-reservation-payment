package com.hospital.payment.publisher;

import com.hospital.payment.config.RabbitConfig;
import com.hospital.payment.dto.PaymentNotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(PaymentNotificationMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.PAYMENT_NOTIFICATION_ROUTING_KEY,
                message
        );
        log.info("[알림 발행] paymentId={}, status={}", message.paymentId(), message.status());
    }
}

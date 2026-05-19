package com.hospital.payment.consumer;

import com.hospital.payment.config.RabbitConfig;
import com.hospital.payment.dto.PaymentRequestMessage;
import com.hospital.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestConsumer {

    private final PaymentService paymentService;

    @RabbitListener(queues = RabbitConfig.PAYMENT_REQUEST_QUEUE)
    public void receive(PaymentRequestMessage message) {
        log.info("[결제 요청 수신] reservationId={}, patientId={}, amount={}",
                message.reservationId(), message.patientId(), message.amount());
        paymentService.processPayment(message);
    }
}

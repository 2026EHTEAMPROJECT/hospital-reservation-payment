package com.hospital.payment.consumer;

import com.hospital.payment.config.RabbitConfig;
import com.hospital.payment.dto.RefundRequestMessage;
import com.hospital.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRefundConsumer {

    private final PaymentService paymentService;

    @RabbitListener(queues = RabbitConfig.PAYMENT_REFUND_QUEUE)
    public void receive(RefundRequestMessage message) {
        log.info("[환불 요청 수신] reservationId={}, patientId={}",
                message.reservationId(), message.patientId());
        paymentService.processRefund(message);
    }
}

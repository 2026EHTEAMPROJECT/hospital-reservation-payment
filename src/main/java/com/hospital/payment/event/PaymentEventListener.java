package com.hospital.payment.event;

import com.hospital.payment.dto.PaymentNotificationMessage;
import com.hospital.payment.publisher.PaymentNotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// DB 커밋 이후에만 알림을 발행하여 트랜잭션-메시지 불일치를 방지한다.
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentNotificationPublisher paymentNotificationPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentCreated(PaymentCreatedEvent event) {
        paymentNotificationPublisher.publish(new PaymentNotificationMessage(
                event.paymentId(),
                event.reservationId(),
                event.patientId(),
                event.status(),
                event.amount(),
                event.patientName()
        ));
    }
}

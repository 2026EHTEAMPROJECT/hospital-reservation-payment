package com.hospital.payment.event;

// 환불 처리 완료 후 발행되는 도메인 이벤트. 트랜잭션 커밋 이후 알림으로 전달된다.
public record PaymentRefundedEvent(
        Long paymentId,
        Long reservationId,
        Long patientId,
        Integer amount,
        String patientName
) {}

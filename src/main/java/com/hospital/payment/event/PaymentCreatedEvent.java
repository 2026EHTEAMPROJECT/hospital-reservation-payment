package com.hospital.payment.event;

public record PaymentCreatedEvent(
        Long paymentId,
        Long reservationId,
        Long patientId,
        String status,
        Integer amount,
        String patientName
) {}

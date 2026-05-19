package com.hospital.payment.dto;

// payment-service -> notification-service
public record PaymentNotificationMessage(
        Long paymentId,
        Long reservationId,
        Long patientId,
        String status,
        Integer amount,
        String patientName
) {}

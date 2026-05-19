package com.hospital.payment.dto;

// booking-service -> payment-service
public record PaymentRequestMessage(
        Long reservationId,
        Long patientId,
        Integer amount,
        String patientName
) {}

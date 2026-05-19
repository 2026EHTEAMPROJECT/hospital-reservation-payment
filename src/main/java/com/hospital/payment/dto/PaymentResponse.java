package com.hospital.payment.dto;

import com.hospital.payment.entity.Payment;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long reservationId,
        Long patientId,
        Integer amount,
        String status,
        LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getReservationId(),
                payment.getPatientId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}

package com.hospital.payment.dto;

// booking-service(예약취소) -> payment-service(환불) 메시지.
// 환불 금액은 reservationId 로 결제내역을 조회해 결정한다.
public record RefundRequestMessage(
        Long reservationId,
        Long patientId
) {
}

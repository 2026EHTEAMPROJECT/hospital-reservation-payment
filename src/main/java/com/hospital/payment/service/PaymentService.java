package com.hospital.payment.service;

import com.hospital.payment.dto.PaymentNotificationMessage;
import com.hospital.payment.dto.PaymentRequestMessage;
import com.hospital.payment.dto.PaymentResponse;
import com.hospital.payment.entity.Payment;
import com.hospital.payment.publisher.PaymentNotificationPublisher;
import com.hospital.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentNotificationPublisher paymentNotificationPublisher;

    @Transactional
    public void processPayment(PaymentRequestMessage request) {
        validate(request);

        if (paymentRepository.existsByReservationId(request.reservationId())) {
            log.warn("[결제 중복 요청] reservationId={} 이미 결제된 예약", request.reservationId());
            return;
        }

        Payment payment = simulatePayment(request);
        Payment saved = paymentRepository.save(payment);

        log.info("[결제 처리 완료] paymentId={}, reservationId={}, status={}",
                saved.getId(), saved.getReservationId(), saved.getStatus());

        paymentNotificationPublisher.publish(new PaymentNotificationMessage(
                saved.getId(),
                saved.getReservationId(),
                saved.getPatientId(),
                saved.getStatus(),
                saved.getAmount(),
                saved.getPatientName()
        ));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. id=" + paymentId));
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByPatient(Long patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("patientId는 null일 수 없습니다.");
        }
        return paymentRepository.findAllByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }

    // 실제 PG사 연동을 가정한 시뮬레이션 (현재는 항상 성공)
    private Payment simulatePayment(PaymentRequestMessage request) {
        String name = request.patientName() != null ? request.patientName() : "환자";
        return Payment.success(request.reservationId(), request.patientId(), name, request.amount());
    }

    private void validate(PaymentRequestMessage request) {
        if (request == null) {
            throw new IllegalArgumentException("결제 요청 메시지는 null일 수 없습니다.");
        }
        if (request.reservationId() == null) {
            throw new IllegalArgumentException("reservationId는 null일 수 없습니다.");
        }
        if (request.patientId() == null) {
            throw new IllegalArgumentException("patientId는 null일 수 없습니다.");
        }
        if (request.amount() == null || request.amount() < 0) {
            throw new IllegalArgumentException("amount는 0 이상이어야 합니다.");
        }
    }
}

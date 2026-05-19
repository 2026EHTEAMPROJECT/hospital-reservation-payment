package com.hospital.payment.repository;

import com.hospital.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationId(Long reservationId);

    List<Payment> findAllByPatientIdOrderByCreatedAtDesc(Long patientId);
}

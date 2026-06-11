package com.hospital.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "payment",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_payment_reservation",
                columnNames = "reservation_id"
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false, length = 20)
    private String status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Payment(Long reservationId, Long patientId, String patientName, Integer amount, String status) {
        this.reservationId = reservationId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.amount = amount;
        this.status = status;
    }

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_REFUNDED = "REFUNDED";

    public static final String LABEL_SUCCESS = "결제완료";
    public static final String LABEL_FAILED = "결제실패";
    public static final String LABEL_REFUNDED = "환불됨";

    public static Payment success(Long reservationId, Long patientId, String patientName, Integer amount) {
        return new Payment(reservationId, patientId, patientName, amount, STATUS_SUCCESS);
    }

    public static Payment failed(Long reservationId, Long patientId, String patientName, Integer amount) {
        return new Payment(reservationId, patientId, patientName, amount, STATUS_FAILED);
    }

    public boolean isSuccess() {
        return STATUS_SUCCESS.equals(this.status);
    }

    public boolean isRefunded() {
        return STATUS_REFUNDED.equals(this.status);
    }

    // 상태 코드값에 대응하는 한국어 표시 라벨을 반환한다.
    // 프론트/관리자가 별도 매핑 없이 그대로 노출할 수 있도록 응답에 함께 내려준다.
    public String getStatusLabel() {
        return switch (this.status) {
            case STATUS_SUCCESS -> LABEL_SUCCESS;
            case STATUS_FAILED -> LABEL_FAILED;
            case STATUS_REFUNDED -> LABEL_REFUNDED;
            default -> this.status;
        };
    }

    // 결제를 환불 상태로 전환한다(이미 환불된 경우 호출하지 않는다 — 멱등 처리는 서비스에서).
    public void markRefunded() {
        this.status = STATUS_REFUNDED;
    }
}

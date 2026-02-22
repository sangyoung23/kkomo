package com.kkomo.kkomo_api.domain.payment;

import com.kkomo.kkomo_api.domain.reservation.Reservation;
import com.kkomo.kkomo_api.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_payments")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    public void changeStatus(PaymentStatus status) {
        this.status = status;
    }
}

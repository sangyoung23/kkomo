package com.kkomo.kkomo_api.domain.payment;

import com.kkomo.kkomo_api.domain.reservation.Reservation;
import com.kkomo.kkomo_api.domain.reservation.ReservationStatus;
import com.kkomo.kkomo_api.global.common.BaseEntity;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
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

    public static Payment create(Reservation reservation, boolean alreadyPaid, BigDecimal amount) {
        if (alreadyPaid) {
            throw new BusinessException(ErrorCode.ALREADY_PAID);
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DEPOSIT_AMOUNT);
        }

        reservation.validatePaymentAmount(amount);

        return Payment.builder()
                .reservation(reservation)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();
    }

    public void complete() {
        if (this.status != PaymentStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATE);
        }

        if (reservation.getStatus() != ReservationStatus.WAITING_PAYMENT) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        this.status = PaymentStatus.SUCCESS;
        reservation.confirm();
    }

    public void fail() {
        if (this.status != PaymentStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATE);
        }

        if (reservation.getStatus() != ReservationStatus.WAITING_PAYMENT) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        this.status = PaymentStatus.FAILED;

        reservation.cancel();
    }

    public void refund(BigDecimal refundAmount) {
        if (this.status != PaymentStatus.SUCCESS) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATE);
        }

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) < 0
                || refundAmount.compareTo(this.amount) > 0) {
            throw new BusinessException(ErrorCode.INVALID_REFUND_AMOUNT);
        }

        this.status = PaymentStatus.REFUNDED;
        this.refundAmount = refundAmount;

        reservation.cancel();
    }
}

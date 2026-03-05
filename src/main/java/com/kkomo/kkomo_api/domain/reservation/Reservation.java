package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.pet.Pet;
import com.kkomo.kkomo_api.domain.shop.Shop;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlot;
import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.global.common.BaseEntity;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", nullable = false, unique = true)
    private TimeSlot timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal depositAmount;

    @Column(nullable = false)
    private LocalDateTime paymentExpireAt;

    public static Reservation create(User user, Pet pet, Shop shop, TimeSlot timeSlot, BigDecimal depositAmount) {

        if (depositAmount == null || depositAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_DEPOSIT_AMOUNT);
        }

        return Reservation.builder()
                .user(user)
                .pet(pet)
                .shop(shop)
                .timeSlot(timeSlot)
                .depositAmount(depositAmount)
                .status(ReservationStatus.WAITING_PAYMENT)
                // TODO
                // 1. LocalDateTime.now().plusMinutes(10) 방식이 아니라 Clock이나 TimeProvider로 변경
                .paymentExpireAt(LocalDateTime.now().plusMinutes(10))
                .build();
    }

    public void validatePaymentAmount(BigDecimal amount) {
        if (!this.depositAmount.equals(amount)) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
    }

    public void validatePayable() {
        if (this.status != ReservationStatus.WAITING_PAYMENT) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATE);
        }
    }

    public void confirm() {
        if (this.status != ReservationStatus.WAITING_PAYMENT) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATE);
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public void complete() {
        if (this.status != ReservationStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATE);
        }
        this.status = ReservationStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status == ReservationStatus.CANCELLED
                || this.status == ReservationStatus.COMPLETED
                || this.status == ReservationStatus.NO_SHOW) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        this.status = ReservationStatus.CANCELLED;

        this.timeSlot.release();
    }

}


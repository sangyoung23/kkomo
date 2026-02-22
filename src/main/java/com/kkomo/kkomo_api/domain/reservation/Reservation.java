package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.pet.Pet;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlot;
import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", nullable = false, unique = true)
    private TimeSlot timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal depositAmount;

    public static Reservation create(User user, Pet pet, TimeSlot timeSlot, BigDecimal depositAmount) {

        if (depositAmount == null || depositAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("보증금은 0 이상이어야 합니다.");
        }

        return Reservation.builder()
                .user(user)
                .pet(pet)
                .timeSlot(timeSlot)
                .depositAmount(depositAmount)
                .status(ReservationStatus.PENDING)
                .build();
    }

    public void confirm() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("예약 상태 변경 불가");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }
}


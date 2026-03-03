package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.timeslot.TimeSlot;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlotStatus;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ReservationTest {

    private TimeSlot createReservedTimeSlot() {
        TimeSlot timeSlot = TimeSlot.create(
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );
        timeSlot.reserve();
        return timeSlot;
    }

    private Reservation createPendingReservation(TimeSlot timeSlot) {
        return Reservation.create(
                null,
                null,
                null,
                timeSlot,
                BigDecimal.valueOf(10000)
        );
    }

    // ===== create() =====

    @Test
    @DisplayName("정상 생성 시 상태는 WAITING_PAYMENT이다")
    void create_success() {
        TimeSlot timeSlot = createReservedTimeSlot();

        Reservation reservation = createPendingReservation(timeSlot);

        assertThat(reservation.getStatus())
                .isEqualTo(ReservationStatus.WAITING_PAYMENT);
    }

    @Test
    @DisplayName("depositAmount가 null이면 예외 발생")
    void create_fail_whenDepositNull() {
        TimeSlot timeSlot = createReservedTimeSlot();

        assertThatThrownBy(() ->
                Reservation.create(null, null, null, timeSlot, null)
        ).isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_DEPOSIT_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("depositAmount가 음수면 예외 발생")
    void create_fail_whenDepositNegative() {
        TimeSlot timeSlot = createReservedTimeSlot();

        assertThatThrownBy(() ->
                Reservation.create(null, null, null, timeSlot, BigDecimal.valueOf(-1))
        ).isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_DEPOSIT_AMOUNT.getMessage());
    }

    // ===== confirm() =====

    @Test
    @DisplayName("PENDING 상태에서 confirm 하면 CONFIRMED로 변경된다")
    void confirm_success() {
        TimeSlot timeSlot = createReservedTimeSlot();
        Reservation reservation = createPendingReservation(timeSlot);

        reservation.confirm();

        assertThat(reservation.getStatus())
                .isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("PENDING이 아닌 상태에서 confirm 하면 예외 발생")
    void confirm_fail_whenNotPending() {
        TimeSlot timeSlot = createReservedTimeSlot();
        Reservation reservation = createPendingReservation(timeSlot);
        reservation.confirm(); // CONFIRMED 상태

        assertThatThrownBy(reservation::confirm)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_RESERVATION_STATE.getMessage());
    }

    // ===== cancel() =====

    @Test
    @DisplayName("PENDING 상태에서 cancel 하면 CANCELLED 되고 타임슬롯이 복구된다")
    void cancel_success() {
        TimeSlot timeSlot = createReservedTimeSlot();
        Reservation reservation = createPendingReservation(timeSlot);

        reservation.cancel();

        assertThat(reservation.getStatus())
                .isEqualTo(ReservationStatus.CANCELLED);

        assertThat(timeSlot.getStatus())
                .isEqualTo(TimeSlotStatus.AVAILABLE);
    }

    @Test
    @DisplayName("CANCELLED 상태에서 cancel 하면 예외 발생")
    void cancel_fail_whenAlreadyCancelled() {
        TimeSlot timeSlot = createReservedTimeSlot();
        Reservation reservation = createPendingReservation(timeSlot);
        reservation.cancel();

        assertThatThrownBy(reservation::cancel)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_RESERVATION_STATE.getMessage());
    }

    @Test
    @DisplayName("COMPLETED 상태에서 cancel 하면 예외 발생")
    void cancel_fail_whenAlreadyCompleted() {
        TimeSlot timeSlot = createReservedTimeSlot();
        Reservation reservation = createPendingReservation(timeSlot);

        reservation.confirm();
        reservation.complete();

        assertThatThrownBy(reservation::cancel)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_RESERVATION_STATE.getMessage());
    }
}


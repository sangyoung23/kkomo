package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.timeslot.TimeSlot;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlotStatus;
import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReservationTest {

    private TimeSlot createReservedTimeSlot() {
        TimeSlot timeSlot = TimeSlot.create(
                null,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1)
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
    @DisplayName("정상 생성 시 결제 만료 시간은 10분 후로 설정된다")
    void createReservation_setPaymentExpireAt() {
        TimeSlot timeSlot = createReservedTimeSlot();

        Reservation reservation = createPendingReservation(timeSlot);

        assertThat(reservation.getPaymentExpireAt())
                .isAfter(LocalDateTime.now().plusMinutes(9));
    }

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

    // ===== complete() =====

    @Test
    @DisplayName("CONFIRMED 상태에서 complete 하면 COMPLETED로 변경된다")
    void complete_success() {
        TimeSlot timeSlot = createReservedTimeSlot();
        Reservation reservation = createPendingReservation(timeSlot);

        reservation.confirm();
        reservation.complete();

        assertThat(reservation.getStatus())
                .isEqualTo(ReservationStatus.COMPLETED);
    }

    @Test
    @DisplayName("CONFIRMED가 아닌 상태에서 complete 하면 예외 발생")
    void complete_fail_whenNotConfirmed() {
        TimeSlot timeSlot = createReservedTimeSlot();
        Reservation reservation = createPendingReservation(timeSlot);

        assertThatThrownBy(reservation::complete)
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

    // ===== validatePaymentAmount() =====

    @Test
    @DisplayName("결제 금액이 일치하면 예외가 발생하지 않는다")
    void validatePaymentAmount_success() {
        TimeSlot timeSlot = createReservedTimeSlot();
        Reservation reservation = createPendingReservation(timeSlot);

        assertThatCode(() ->
                reservation.validatePaymentAmount(BigDecimal.valueOf(10000)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("결제 금액이 다르면 예외 발생")
    void validatePaymentAmount_fail() {
        TimeSlot timeSlot = createReservedTimeSlot();
        Reservation reservation = createPendingReservation(timeSlot);

        assertThatThrownBy(() ->
                reservation.validatePaymentAmount(BigDecimal.valueOf(5000)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PAYMENT_AMOUNT.getMessage());
    }

    // ===== validatePayable() =====

    @Test
    @DisplayName("WAITING_PAYMENT 상태이면 결제 가능")
    void validatePayable_success() {
        TimeSlot timeSlot = createReservedTimeSlot();
        Reservation reservation = createPendingReservation(timeSlot);

        assertThatCode(reservation::validatePayable)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("WAITING_PAYMENT 상태가 아니면 결제 불가")
    void validatePayable_fail() {
        TimeSlot timeSlot = createReservedTimeSlot();
        Reservation reservation = createPendingReservation(timeSlot);

        reservation.confirm();

        assertThatThrownBy(reservation::validatePayable)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_RESERVATION_STATE.getMessage());
    }

    // ===== validateCancelable() =====

    @Test
    @DisplayName("예약 시작 24시간 이전이면 취소 가능")
    void validateCancelable_success() {

        TimeSlot timeSlot = TimeSlot.create(
                null,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1)
        );
        timeSlot.reserve();

        Reservation reservation = createPendingReservation(timeSlot);

        assertThatCode(reservation::validateCancelable)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("예약 시작 24시간 이내이면 취소 불가")
    void validateCancelable_fail_whenWithin24Hours() {

        TimeSlot timeSlot = TimeSlot.create(
                null,
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11)
        );
        timeSlot.reserve();

        Reservation reservation = createPendingReservation(timeSlot);

        assertThatThrownBy(reservation::validateCancelable)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CANCEL_NOT_ALLOWED.getMessage());
    }

    // ===== validateCancelAuthority() =====

    @Test
    @DisplayName("예약자가 아니면 취소 권한 없음")
    void validateCancelAuthority_fail() {

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        TimeSlot timeSlot = createReservedTimeSlot();

        Reservation reservation = Reservation.create(
                user,
                null,
                null,
                timeSlot,
                BigDecimal.valueOf(10000)
        );

        assertThatThrownBy(() ->
                reservation.validateCancelAuthority(2L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ACCESS_DENIED.getMessage());
    }
}


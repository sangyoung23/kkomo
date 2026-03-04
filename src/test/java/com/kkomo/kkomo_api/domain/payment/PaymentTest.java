package com.kkomo.kkomo_api.domain.payment;

import com.kkomo.kkomo_api.domain.reservation.Reservation;
import com.kkomo.kkomo_api.domain.reservation.ReservationStatus;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlot;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class PaymentTest {

    // ===== 테스트용 팩토리 =====
    private static class TestFactory {

        static Reservation createReservationWithStatus(ReservationStatus status) {
            TimeSlot timeSlot = createReservedTimeSlot();
            Reservation reservation = Reservation.create(null, null, null, timeSlot, BigDecimal.valueOf(10000));

            try {
                var statusField = Reservation.class.getDeclaredField("status");
                statusField.setAccessible(true);
                statusField.set(reservation, status);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return reservation;
        }

        static TimeSlot createReservedTimeSlot() {
            TimeSlot timeSlot = TimeSlot.create(null, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            timeSlot.reserve();
            return timeSlot;
        }

        static Reservation createPendingReservation() {
            return Reservation.create(null, null, null, createReservedTimeSlot(), BigDecimal.valueOf(10000));
        }

        static Reservation createConfirmedReservation() {
            Reservation reservation = createPendingReservation();
            reservation.confirm();
            return reservation;
        }

        static Payment createPendingPayment() {
            return Payment.create(createPendingReservation(), false, BigDecimal.valueOf(10000));
        }

        static Payment createPendingPaymentWithReservation(Reservation reservation) {
            return Payment.create(reservation, false, BigDecimal.valueOf(10000));
        }
    }

    // ===== create() =====
    @Test
    @DisplayName("정상 생성 시 상태는 PENDING이다")
    void create_success() {
        Payment payment = TestFactory.createPendingPayment();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("amount가 null이면 예외 발생")
    void create_fail_whenAmountNull() {
        Reservation reservation = TestFactory.createPendingReservation();

        assertThatThrownBy(() -> Payment.create(reservation, false, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_DEPOSIT_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("amount가 음수면 예외 발생")
    void create_fail_whenAmountNegative() {
        Reservation reservation = TestFactory.createPendingReservation();

        assertThatThrownBy(() -> Payment.create(reservation, false, BigDecimal.valueOf(-1)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_DEPOSIT_AMOUNT.getMessage());
    }

    // ===== complete() =====
    @Test
    @DisplayName("PENDING 상태 + 예약 WAITING_PAYMENT -> complete() 성공")
    void complete_success() {
        Payment payment = TestFactory.createPendingPayment();
        payment.complete();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getReservation().getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("PENDING 상태가 아닌 Payment -> complete() 예외")
    void complete_fail_whenPaymentNotPending() {
        Payment payment = TestFactory.createPendingPayment();
        payment.complete(); // SUCCESS 상태로 변경

        assertThatThrownBy(payment::complete)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PAYMENT_STATE.getMessage());
    }

    @Test
    @DisplayName("Reservation 상태가 WAITING_PAYMENT가 아닐 때 complete() 예외")
    void complete_fail_whenReservationNotWaitingPayment() {
        Reservation reservation = TestFactory.createConfirmedReservation();
        Payment payment = TestFactory.createPendingPaymentWithReservation(reservation);

        assertThatThrownBy(payment::complete)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_RESERVATION_STATE.getMessage());
    }

    // ===== fail() =====
    @Test
    @DisplayName("PENDING 상태 + 예약 WAITING_PAYMENT -> fail() 성공")
    void fail_success() {
        Payment payment = TestFactory.createPendingPayment();
        payment.fail();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getReservation().getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    @DisplayName("PENDING이 아닌 Payment -> fail() 예외")
    void fail_fail_whenPaymentNotPending() {
        Payment payment = TestFactory.createPendingPayment();
        payment.fail(); // FAILED 상태

        assertThatThrownBy(payment::fail)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PAYMENT_STATE.getMessage());
    }

    @Test
    @DisplayName("Reservation 상태가 WAITING_PAYMENT가 아닐 때 fail() 예외")
    void fail_fail_whenReservationNotWaitingPayment() {
        Reservation reservation = TestFactory.createConfirmedReservation();
        Payment payment = TestFactory.createPendingPaymentWithReservation(reservation);

        assertThatThrownBy(payment::fail)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_RESERVATION_STATE.getMessage());
    }

    // ===== refund() =====
    @Test
    @DisplayName("SUCCESS 상태 + 예약 CONFIRMED -> refund() 성공")
    void refund_success() throws Exception {
        Reservation reservation = TestFactory.createReservationWithStatus(ReservationStatus.WAITING_PAYMENT);
        Payment payment = Payment.create(reservation, false, BigDecimal.valueOf(10000));

        payment.complete();

        payment.refund(BigDecimal.valueOf(10000));

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(payment.getRefundAmount()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(payment.getReservation().getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    @DisplayName("환불 금액이 null이면 예외 발생")
    void refund_fail_whenAmountNull() {
        Reservation reservation = TestFactory.createReservationWithStatus(ReservationStatus.WAITING_PAYMENT);
        Payment payment = TestFactory.createPendingPaymentWithReservation(reservation);
        payment.complete();

        assertThatThrownBy(() -> payment.refund(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_REFUND_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("환불 금액이 음수이면 예외 발생")
    void refund_fail_whenAmountNegative() {
        Reservation reservation = TestFactory.createReservationWithStatus(ReservationStatus.WAITING_PAYMENT);
        Payment payment = TestFactory.createPendingPaymentWithReservation(reservation);
        payment.complete();

        assertThatThrownBy(() -> payment.refund(BigDecimal.valueOf(-1)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_REFUND_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("환불 금액이 결제 금액보다 크면 예외 발생")
    void refund_fail_whenAmountExceedsPayment() {
        Reservation reservation = TestFactory.createReservationWithStatus(ReservationStatus.WAITING_PAYMENT);
        Payment payment = TestFactory.createPendingPaymentWithReservation(reservation);
        payment.complete();

        assertThatThrownBy(() -> payment.refund(BigDecimal.valueOf(20000)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_REFUND_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("Payment 상태가 SUCCESS가 아니면 refund() 예외 발생")
    void refund_fail_whenPaymentNotSuccess() {
        Reservation reservation = TestFactory.createReservationWithStatus(ReservationStatus.CONFIRMED);
        Payment payment = Payment.create(reservation, false, BigDecimal.valueOf(10000));

        assertThatThrownBy(() -> payment.refund(BigDecimal.valueOf(5000)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PAYMENT_STATE.getMessage());
    }

    @Test
    @DisplayName("Reservation 상태가 CONFIRMED가 아니면 refund() 예외 발생")
    void refund_fail_whenReservationNotConfirmed() throws Exception {
        Reservation reservation = TestFactory.createReservationWithStatus(ReservationStatus.WAITING_PAYMENT);
        Payment payment = Payment.create(reservation, false, BigDecimal.valueOf(10000));

        var statusField = Payment.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(payment, PaymentStatus.SUCCESS);

        assertThatThrownBy(() -> payment.refund(BigDecimal.valueOf(5000)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_RESERVATION_STATE.getMessage());
    }
}
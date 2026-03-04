package com.kkomo.kkomo_api.domain.payment;

import com.kkomo.kkomo_api.domain.reservation.Reservation;
import com.kkomo.kkomo_api.domain.reservation.ReservationRepository;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock PaymentRepository paymentRepository;
    @Mock ReservationRepository reservationRepository;

    @InjectMocks
    PaymentService paymentService;

    // ===== requestPayment() =====

    @Test
    @DisplayName("결제 요청 성공")
    void requestPayment_success() {

        // given
        Long reservationId = 1L;
        BigDecimal amount = BigDecimal.valueOf(10000);

        Reservation reservation = mock(Reservation.class);

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.of(reservation));

        Payment savedPayment = mock(Payment.class);

        when(paymentRepository.save(any()))
                .thenReturn(savedPayment);

        // when
        Payment result = paymentService.requestPayment(reservationId, amount);

        // then
        assertThat(result).isEqualTo(savedPayment);
        verify(paymentRepository).save(any());
    }

    @Test
    @DisplayName("예약이 존재하지 않으면 결제 요청 시 예외 발생")
    void requestPayment_fail_reservationNotFound() {

        // given
        Long reservationId = 1L;
        BigDecimal amount = BigDecimal.valueOf(10000);

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                paymentService.requestPayment(reservationId, amount)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.RESERVATION_NOT_FOUND.getMessage());
    }

    // ===== completePayment() =====

    @Test
    @DisplayName("결제 성공 처리")
    void completePayment_success() {

        // given
        Long paymentId = 1L;

        Payment payment = mock(Payment.class);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        // when
        paymentService.completePayment(paymentId);

        // then
        verify(payment).complete();
    }

    @Test
    @DisplayName("결제가 존재하지 않으면 예외 발생")
    void completePayment_fail_paymentNotFound() {

        // given
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                paymentService.completePayment(paymentId)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PAYMENT_NOT_FOUND.getMessage());
    }

    // ===== failPayment() =====

    @Test
    @DisplayName("결제 실패 처리")
    void failPayment_success() {

        // given
        Long paymentId = 1L;

        Payment payment = mock(Payment.class);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        // when
        paymentService.failPayment(paymentId);

        // then
        verify(payment).fail();
    }

    @Test
    @DisplayName("결제가 존재하지 않으면 실패 처리 시 예외 발생")
    void failPayment_fail_paymentNotFound() {

        // given
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                paymentService.failPayment(paymentId)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PAYMENT_NOT_FOUND.getMessage());
    }

    // ===== refundPayment() =====

    @Test
    @DisplayName("환불 처리 성공")
    void refundPayment_success() {

        // given
        Long paymentId = 1L;
        BigDecimal refundAmount = BigDecimal.valueOf(5000);

        Payment payment = mock(Payment.class);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        // when
        paymentService.refundPayment(paymentId, refundAmount);

        // then
        verify(payment).refund(refundAmount);
    }

    @Test
    @DisplayName("결제가 존재하지 않으면 환불 처리 시 예외 발생")
    void refundPayment_fail_paymentNotFound() {

        // given
        Long paymentId = 1L;
        BigDecimal refundAmount = BigDecimal.valueOf(5000);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                paymentService.refundPayment(paymentId, refundAmount)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PAYMENT_NOT_FOUND.getMessage());
    }
}
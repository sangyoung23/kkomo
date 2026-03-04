package com.kkomo.kkomo_api.domain.payment;

import com.kkomo.kkomo_api.domain.reservation.Reservation;
import com.kkomo.kkomo_api.domain.reservation.ReservationRepository;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    // 결제 요청
    @Transactional
    public Payment requestPayment(Long reservationId, BigDecimal amount) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.validatePayable();

        boolean alreadyPaid = paymentRepository.existsByReservationAndStatus(reservation, PaymentStatus.SUCCESS);

        Payment payment = Payment.create(
                reservation, alreadyPaid, amount
        );

        return paymentRepository.save(payment);
    }

    // TODO
    // 1. 결제 요청 -> PG 결제 진행 -> PG 콜백 -> completePayment 호출
    // 결제 성공
    @Transactional
    public void completePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.complete();
    }

    // 결제 실패
    @Transactional
    public void failPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.fail();
    }

    // 환불 처리
    @Transactional
    public void refundPayment(Long paymentId, BigDecimal refundAmount) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.refund(refundAmount);
    }

}
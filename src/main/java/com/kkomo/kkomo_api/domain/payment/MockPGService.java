package com.kkomo.kkomo_api.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MockPGService {

    private final PaymentService paymentService;

    // 결제 성공 시뮬레이션
    public void simulateSuccess(Long paymentId) {
        paymentService.completePayment(paymentId);
        System.out.println("MockPG: Payment " + paymentId + " SUCCESS");
    }

    // 결제 실패 시뮬레이션
    public void simulateFail(Long paymentId) {
        paymentService.failPayment(paymentId);
        System.out.println("MockPG: Payment " + paymentId + " FAILED");
    }

    // 환불 시뮬레이션
    public void simulateRefund(Long paymentId, java.math.BigDecimal amount) {
        paymentService.refundPayment(paymentId, amount);
        System.out.println("MockPG: Payment " + paymentId + " REFUNDED " + amount);
    }
}
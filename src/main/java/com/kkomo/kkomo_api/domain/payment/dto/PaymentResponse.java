package com.kkomo.kkomo_api.domain.payment.dto;

import com.kkomo.kkomo_api.domain.payment.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private String status;
    private BigDecimal amount;

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getStatus().name(),
                payment.getAmount()
        );
    }
}
package com.kkomo.kkomo_api.domain.payment.dto;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class PaymentRequest {

    private Long reservationId;
    private BigDecimal amount;
}
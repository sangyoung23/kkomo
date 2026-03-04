package com.kkomo.kkomo_api.domain.payment.dto;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class RefundRequest {

    private BigDecimal amount;
}
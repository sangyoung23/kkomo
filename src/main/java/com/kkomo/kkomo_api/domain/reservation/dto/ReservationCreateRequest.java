package com.kkomo.kkomo_api.domain.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationCreateRequest {

    private Long userId;
    private Long petId;
    private Long shopId;
    private Long timeSlotId;
    private BigDecimal depositAmount;
}

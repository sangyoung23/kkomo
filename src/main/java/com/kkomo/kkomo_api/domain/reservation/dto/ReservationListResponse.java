package com.kkomo.kkomo_api.domain.reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationListResponse(
        Long reservationId,
        String petName,
        String userName,
        LocalDateTime reservedAt,
        BigDecimal depositAmount,
        String status
) {}

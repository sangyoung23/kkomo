package com.kkomo.kkomo_api.domain.reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerReservationListResponse(
        Long reservationId,
        String shopName,
        String petName,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        BigDecimal depositAmount,
        String status
) {}


package com.kkomo.kkomo_api.domain.reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OwnerReservationListResponse(
        Long reservationId,
        String userName,
        String userPhoneNumber,
        String petName,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        BigDecimal depositAmount,
        String status
) {}

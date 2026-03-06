package com.kkomo.kkomo_api.domain.reservation.dto;

import com.kkomo.kkomo_api.domain.reservation.Reservation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
public class ReservationDetailResponse {

    private Long reservationId;
    private String userName;
    private String petName;
    private LocalDateTime reservedStartAt;
    private LocalDateTime reservedEndAt;
    private BigDecimal depositAmount;
    private String status;

    public static ReservationDetailResponse from(Reservation reservation) {
        return ReservationDetailResponse.builder()
                .reservationId(reservation.getId())
                .userName(reservation.getUser().getName())
                .petName(reservation.getPet().getName())
                .reservedStartAt(reservation.getTimeSlot().getStartDateTime())
                .reservedEndAt(reservation.getTimeSlot().getEndDateTime())
                .depositAmount(reservation.getDepositAmount())
                .status(reservation.getStatus().name())
                .build();
    }
}

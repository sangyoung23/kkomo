package com.kkomo.kkomo_api.domain.reservation;

public enum ReservationStatus {
    PENDING,        // 예약 요청
    CONFIRMED,      // 예약 확정
    CANCELLED,      // 취소
    COMPLETED,      // 시술 완료
    NO_SHOW        // 노쇼
    }

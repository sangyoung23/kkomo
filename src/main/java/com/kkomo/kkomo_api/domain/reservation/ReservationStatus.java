package com.kkomo.kkomo_api.domain.reservation;

public enum ReservationStatus {
    WAITING_PAYMENT,   // 결제 대기
    CONFIRMED,         // 결제 완료 → 예약 확정
    CANCELLED,         // 취소
    COMPLETED,         // 시술 완료
    NO_SHOW            // 노쇼
    }

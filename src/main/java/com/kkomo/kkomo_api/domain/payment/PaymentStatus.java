package com.kkomo.kkomo_api.domain.payment;

public enum PaymentStatus {
    PENDING,    // 결제 대기 (예약 생성 후, 결제 전)
    SUCCESS,    // 결제 완료 (예약 확정 시)
    FAILED,     // 결제 실패
    REFUNDED    // 환불 완료
}

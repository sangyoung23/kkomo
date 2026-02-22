package com.kkomo.kkomo_api.domain.payment;

public enum PaymentStatus {
    PENDING, // 결제 대기
    CANCELLED, //결제 취소
    PAID, // 결제 완료
    REFUNDED, // 환불 완료
    FAILED // 결제 실패
}

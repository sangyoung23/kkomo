package com.kkomo.kkomo_api.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // TIME_SLOT (2000번대)
    TIME_SLOT_NOT_FOUND(2001, HttpStatus.NOT_FOUND, "예약 슬롯이 존재하지 않습니다."),
    TIME_SLOT_ALREADY_RESERVED(2002, HttpStatus.BAD_REQUEST, "이미 예약된 슬롯입니다."),
    TIME_SLOT_BLOCKED(2003, HttpStatus.FORBIDDEN, "해당 슬롯은 예약할 수 없습니다."),
    INVALID_TIME_SLOT_STATE(2004, HttpStatus.BAD_REQUEST, "슬롯 상태가 올바르지 않습니다."),

    // USER (3000번대)
    USER_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // PET (4000번대)
    PET_NOT_FOUND(4001, HttpStatus.NOT_FOUND, "펫을 찾을 수 없습니다."),

    // RESERVATION (5000번대)
    RESERVATION_NOT_FOUND(5001, HttpStatus.NOT_FOUND, "예약 내역이 존재하지 않습니다."),
    INVALID_RESERVATION_STATE(5002, HttpStatus.BAD_REQUEST, "예약 상태가 올바르지 않습니다."),
    INVALID_DEPOSIT_AMOUNT(5003, HttpStatus.BAD_REQUEST, "보증금은 0 이상이어야 합니다."),

    // SHOP (6000번대)
    SHOP_NOT_FOUND(6001, HttpStatus.NOT_FOUND, "샵을 찾을 수 없습니다.");

    private final int code;           // 비즈니스 코드
    private final HttpStatus status;  // HTTP 상태코드
    private final String message;     // 에러 메시지
}

package com.kkomo.kkomo_api.domain.timeslot;

public enum TimeSlotStatus {
    AVAILABLE, // 예약 가능
    RESERVED, // 예약 완료
    BLOCKED // 관리자/샵이 막음
}

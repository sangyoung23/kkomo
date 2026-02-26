package com.kkomo.kkomo_api.domain.timeSlot;

import com.kkomo.kkomo_api.domain.timeslot.TimeSlot;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlotStatus;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;


class TimeSlotTest {

    private TimeSlot createAvailableTimeSlot() {
        return TimeSlot.create(
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );
    }

    // ===== validateReservable() =====

    @Test
    @DisplayName("예약 가능한 타임슬롯은 validateReservable 통과한다.")
    void validateReservable_success() {
        TimeSlot timeSlot = createAvailableTimeSlot();

        assertThatCode(timeSlot::validateReservable)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이미 예약된 타임슬롯은 validateReservable 호출 시 예외 발생")
    void validateReservable_fail_reserved() {
        TimeSlot timeSlot = createAvailableTimeSlot();
        timeSlot.reserve();

        assertThatThrownBy(timeSlot::validateReservable)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.TIME_SLOT_ALREADY_RESERVED.getMessage());
    }


    // ===== reserve() =====

    @Test
    @DisplayName("reserve 호출하면 상태가 RESERVED 상태로 변경된다")
    void reserve_success() {
        TimeSlot timeSlot = createAvailableTimeSlot();

        timeSlot.reserve();

        assertThat(timeSlot.getStatus())
                .isEqualTo(TimeSlotStatus.RESERVED);
    }


    // ===== release() =====

    @Test
    @DisplayName("release 호출하면 상태가 AVAILABLE 상태로 변경된다")
    void release_success() {
        TimeSlot timeSlot = createAvailableTimeSlot();
        timeSlot.reserve();

        timeSlot.release();

        assertThat(timeSlot.getStatus())
                .isEqualTo(TimeSlotStatus.AVAILABLE);
    }

    @Test
    @DisplayName("예약 상태가 아닐 때 release 호출하면 예외 발생")
    void release_fail_whenNotReserved() {
        TimeSlot timeSlot = createAvailableTimeSlot();

        assertThatThrownBy(timeSlot::release)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_TIME_SLOT_STATE.getMessage());
    }
}


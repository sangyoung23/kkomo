package com.kkomo.kkomo_api.domain.timeslot;

import com.kkomo.kkomo_api.domain.shop.Shop;
import com.kkomo.kkomo_api.global.common.BaseEntity;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tb_time_slots",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_shop_start_end",
                        columnNames = {"shop_id", "start_date_time", "end_date_time"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TimeSlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_slot_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TimeSlotStatus status;

    public void validateReservable() {
        if (this.status == TimeSlotStatus.RESERVED) {
            throw new BusinessException(ErrorCode.TIME_SLOT_ALREADY_RESERVED);
        }
        if (this.status == TimeSlotStatus.BLOCKED) {
            throw new BusinessException(ErrorCode.TIME_SLOT_BLOCKED);
        }
    }

    public void reserve() {
        validateReservable();
        this.status = TimeSlotStatus.RESERVED;
    }

    public void release() {
        this.status = TimeSlotStatus.AVAILABLE;
    }
}


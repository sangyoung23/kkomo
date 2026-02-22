package com.kkomo.kkomo_api.domain.timeslot;

import com.kkomo.kkomo_api.domain.shop.Shop;
import com.kkomo.kkomo_api.global.common.BaseEntity;
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
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TimeSlotStatus status = TimeSlotStatus.AVAILABLE;

    public void assignShop(Shop shop) {
        this.shop = shop;
    }

    public void changeStatus(TimeSlotStatus status) {
        this.status = status;
    }
}

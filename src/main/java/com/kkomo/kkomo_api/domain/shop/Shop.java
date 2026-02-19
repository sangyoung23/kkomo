package com.kkomo.kkomo_api.domain.shop;

import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "tb_shops")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal depositAmount;

    @Column(nullable = false)
    private Integer cancelFreeHours;

    @Column(nullable = false)
    private LocalTime openTime;

    @Column(nullable = false)
    private LocalTime closeTime;

    @Lob
    @Column(nullable = true)
    private String description;

    @Column(nullable = false, precision = 10, scale = 7)
    private Double latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private Double longitude;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShopStatus status = ShopStatus.PENDING;

    public void assignOwner(User owner) {
        this.owner = owner;
    }

    public void changeStatus(ShopStatus status) {
        this.status = status;
    }
}

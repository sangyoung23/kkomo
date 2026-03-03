package com.kkomo.kkomo_api;

import com.kkomo.kkomo_api.domain.shop.Shop;
import com.kkomo.kkomo_api.domain.shop.ShopStatus;
import com.kkomo.kkomo_api.domain.user.User;

import java.math.BigDecimal;
import java.time.LocalTime;

public class ShopFixture {

    public static Shop shop(User owner) {
        return Shop.builder()
                .owner(owner)
                .name("테스트샵")
                .address("서울시 어딘가")
                .phoneNumber("02-000-0000")
                .depositAmount(BigDecimal.valueOf(10000))
                .cancelFreeHours(24)
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(18, 0))
                .description("테스트용 샵")
                .latitude(new BigDecimal("37.1234567"))
                .longitude(new BigDecimal("127.1234567"))
                .status(ShopStatus.PENDING)
                .build();
    }
}

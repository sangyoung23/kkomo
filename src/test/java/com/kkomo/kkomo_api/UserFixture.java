package com.kkomo.kkomo_api;

import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.domain.user.UserRole;

public class UserFixture {

    public static User customer() {
        return User.builder()
                .email("user@test.com")
                .password("password")
                .phoneNumber("010-0000-0000")
                .name("테스트유저")
                .role(UserRole.CUSTOMER)
                .build();
    }

    public static User owner() {
        return User.builder()
                .email("owner@test.com")
                .password("password")
                .phoneNumber("010-1111-1111")
                .name("테스트사장")
                .role(UserRole.OWNER)
                .build();
    }
}

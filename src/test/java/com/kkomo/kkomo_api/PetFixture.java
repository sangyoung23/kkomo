package com.kkomo.kkomo_api;

import com.kkomo.kkomo_api.domain.pet.Gender;
import com.kkomo.kkomo_api.domain.pet.Pet;
import com.kkomo.kkomo_api.domain.user.User;

import java.math.BigDecimal;

public class PetFixture {

    public static Pet pet(User user) {
        return Pet.builder()
                .user(user)
                .name("멍멍이")
                .breed("푸들")
                .weight(BigDecimal.valueOf(3.5))
                .gender(Gender.MALE)
                .neutered(true)
                .personality("활발함")
                .build();
    }
}

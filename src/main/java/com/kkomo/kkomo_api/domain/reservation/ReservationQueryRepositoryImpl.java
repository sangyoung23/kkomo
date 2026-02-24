package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.pet.QPet;
import com.kkomo.kkomo_api.domain.reservation.dto.ReservationListResponse;
import com.kkomo.kkomo_api.domain.shop.QShop;
import com.kkomo.kkomo_api.domain.timeslot.QTimeSlot;
import com.kkomo.kkomo_api.domain.user.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReservationQueryRepositoryImpl implements ReservationQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReservationListResponse> findReservationList(Long shopId, Long userId) {

        QReservation reservation = QReservation.reservation;
        QUser user = QUser.user;
        QPet pet = QPet.pet;
        QShop shop = QShop.shop;
        QTimeSlot timeSlot = QTimeSlot.timeSlot;

        return queryFactory
                .select(Projections.constructor(ReservationListResponse.class,
                        reservation.id,
                        pet.name,
                        user.name,
                        shop.id,
                        user.phoneNumber,
                        timeSlot.startDateTime,
                        timeSlot.endDateTime,
                        reservation.depositAmount,
                        reservation.status.stringValue()
                ))
                .from(reservation)
                .join(reservation.user, user)
                .join(reservation.pet, pet)
                .join(reservation.shop, shop)
                .join(reservation.timeSlot, timeSlot)
                .where(reservation.shop.id.eq(shopId).and(reservation.user.id.eq(userId)))
                .fetch();
    }
}


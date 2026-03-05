package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.pet.QPet;
import com.kkomo.kkomo_api.domain.reservation.dto.CustomerReservationListResponse;
import com.kkomo.kkomo_api.domain.reservation.dto.OwnerReservationListResponse;
import com.kkomo.kkomo_api.domain.shop.QShop;
import com.kkomo.kkomo_api.domain.timeslot.QTimeSlot;
import com.kkomo.kkomo_api.domain.user.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationQueryRepositoryImpl implements ReservationQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CustomerReservationListResponse> getCustomerReservations(Long userId, Pageable pageable) {

        QReservation reservation = QReservation.reservation;
        QPet pet = QPet.pet;
        QShop shop = QShop.shop;
        QTimeSlot timeSlot = QTimeSlot.timeSlot;

        List<CustomerReservationListResponse> content = queryFactory
                .select(Projections.constructor(CustomerReservationListResponse.class,
                        reservation.id,
                        shop.name,
                        pet.name,
                        timeSlot.startDateTime,
                        timeSlot.endDateTime,
                        reservation.depositAmount,
                        reservation.status.stringValue()
                ))
                .from(reservation)
                .join(reservation.shop, shop)
                .join(reservation.pet, pet)
                .join(reservation.timeSlot, timeSlot)
                .where(reservation.user.id.eq(userId))
                .orderBy(reservation.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(reservation.user.id.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public Page<OwnerReservationListResponse> getOwnerReservations(Long shopId, Pageable pageable) {

        QReservation reservation = QReservation.reservation;
        QUser user = QUser.user;
        QPet pet = QPet.pet;
        QTimeSlot timeSlot = QTimeSlot.timeSlot;

        List<OwnerReservationListResponse> content = queryFactory
                .select(Projections.constructor(OwnerReservationListResponse.class,
                        reservation.id,
                        user.name,
                        user.phoneNumber,
                        pet.name,
                        timeSlot.startDateTime,
                        timeSlot.endDateTime,
                        reservation.depositAmount,
                        reservation.status.stringValue()
                ))
                .from(reservation)
                .join(reservation.user, user)
                .join(reservation.pet, pet)
                .join(reservation.timeSlot, timeSlot)
                .where(reservation.shop.id.eq(shopId))
                .orderBy(timeSlot.startDateTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(reservation.shop.id.eq(shopId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<Reservation> getExpiredReservations(LocalDateTime now) {

        QReservation reservation = QReservation.reservation;

        return queryFactory
                .selectFrom(reservation)
                .where(
                        reservation.status.eq(ReservationStatus.WAITING_PAYMENT),
                        reservation.paymentExpireAt.before(now)
                )
                .fetch();
    }

    @Override
    public List<Reservation> getNoShowReservations(LocalDateTime now) {

        QReservation reservation = QReservation.reservation;
        QTimeSlot timeSlot = QTimeSlot.timeSlot;

        return queryFactory
                .selectFrom(reservation)
                .join(reservation.timeSlot, timeSlot)
                .where(
                        reservation.status.eq(ReservationStatus.CONFIRMED),
                        timeSlot.startDateTime.before(now.minusMinutes(15))
                )
                .fetch();
    }
}


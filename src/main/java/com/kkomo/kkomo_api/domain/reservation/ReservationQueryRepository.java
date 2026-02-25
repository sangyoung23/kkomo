package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.reservation.dto.CustomerReservationListResponse;
import com.kkomo.kkomo_api.domain.reservation.dto.OwnerReservationListResponse;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface ReservationQueryRepository {
    // 고객용 예약 목록 조회
    Page<CustomerReservationListResponse> getCustomerReservations(Long userId, Pageable pageable);
    // 사장용 예약 목록 조회
    Page<OwnerReservationListResponse> getOwnerReservations(Long shopId, Pageable pageable);
}


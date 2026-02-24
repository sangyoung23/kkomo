package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.reservation.dto.ReservationListResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationQueryRepository {
    // 예약 목록 조회
    List<ReservationListResponse> findReservationList(Long shopId, Long userId);
}


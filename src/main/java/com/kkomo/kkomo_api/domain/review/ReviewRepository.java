package com.kkomo.kkomo_api.domain.review;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 샵 기준 예약 조회
    @EntityGraph(attributePaths = {"reservation", "reservation.user", "reservation.pet"})
    List<Review> findByReservationShopId(Long shopId);
}

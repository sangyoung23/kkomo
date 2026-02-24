package com.kkomo.kkomo_api.domain.reservation;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 예약 상세 조회
    @EntityGraph(attributePaths = {"user", "pet", "timeSlot"})
    Optional<Reservation> findWithUserAndPetAndTimeSlotById(Long id);
}

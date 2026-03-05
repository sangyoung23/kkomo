package com.kkomo.kkomo_api.domain.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationQueryRepository reservationQueryRepository;

    // TODO
    // 1. MVP 이후에 Redis Lock, kafka 등 이벤트 큐 기반 구조 변경
    // 2. 예약 취소가 됐을 때 화면에서 알림 ? 또는 카톡알림 ? 보내주기

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cancelExpiredReservations() {
        List<Reservation> expiredReservations = reservationQueryRepository.getExpiredReservations(LocalDateTime.now());

        for (Reservation reservation : expiredReservations) {
            reservation.cancel();
            log.info("예약 결제 타임아웃 취소 reservationId={}", reservation.getId());
        }
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void processNoShowReservations() {

        List<Reservation> noShowReservations =
                reservationQueryRepository.getNoShowReservations(LocalDateTime.now());

        for (Reservation reservation : noShowReservations) {
            reservation.noShow();
            log.info("노쇼 처리 reservationId={}", reservation.getId());
        }
    }
}

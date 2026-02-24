package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.reservation.dto.ReservationCreateRequest;
import com.kkomo.kkomo_api.domain.reservation.dto.ReservationDetailResponse;
import com.kkomo.kkomo_api.domain.reservation.dto.ReservationListResponse;
import com.kkomo.kkomo_api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 목록 조회
    // TODO
    // 1. Spring Security 적용 후 AuthenticationPrincipal 방식으로 변경
    @GetMapping
    public ApiResponse<List<ReservationListResponse>> getReservations(
            @RequestParam Long shopId,
            @RequestParam Long userId) {
        return ApiResponse.success(reservationService.getReservations(shopId, userId));
    }

    // 예약 상세 조회
    @GetMapping("/{reservationId}")
    public ApiResponse<ReservationDetailResponse> getReservationDetail(@PathVariable Long reservationId) {
        return ApiResponse.success(reservationService.getReservationDetail(reservationId));
    }

    // 예약 생성
    @PostMapping
    public ApiResponse<Long> createReservation(@RequestBody ReservationCreateRequest request) {
        return ApiResponse.success(reservationService.createReservation(request));
    }

    // 예약 확정
    @PostMapping("/{reservationId}/confirm")
    public ApiResponse<Long> confirmReservation(@PathVariable Long reservationId) {
        return ApiResponse.success(reservationService.confirmReservation(reservationId));
    }

    // 예약 취소
    @PostMapping("/{reservationId}/cancel")
    public ApiResponse<Long> cancelReservation(@PathVariable Long reservationId) {
        return ApiResponse.success(reservationService.cancelReservation(reservationId));
    }
}

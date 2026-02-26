package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.pet.Pet;
import com.kkomo.kkomo_api.domain.pet.PetRepository;
import com.kkomo.kkomo_api.domain.reservation.dto.CustomerReservationListResponse;
import com.kkomo.kkomo_api.domain.reservation.dto.OwnerReservationListResponse;
import com.kkomo.kkomo_api.domain.reservation.dto.ReservationCreateRequest;
import com.kkomo.kkomo_api.domain.reservation.dto.ReservationDetailResponse;
import com.kkomo.kkomo_api.domain.shop.Shop;
import com.kkomo.kkomo_api.domain.shop.ShopRepository;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlot;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlotRepository;
import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.domain.user.UserRepository;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final ShopRepository shopRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationQueryRepository reservationQueryRepository;

    // 고객용 예약 목록 조회
    @Transactional(readOnly = true)
    public Page<CustomerReservationListResponse> getCustomerReservations(Long userId, Pageable pageable) {
        return reservationQueryRepository.getCustomerReservations(userId, pageable);
    }

    // 사장용 예약 목록 조회
    @Transactional(readOnly = true)
    public Page<OwnerReservationListResponse> getOwnerReservations(Long shopId, Pageable pageable) {
        return reservationQueryRepository.getOwnerReservations(shopId, pageable);
    }

    // 예약 상세 조회
    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservationDetail(Long reservationId) {
        Reservation reservation = reservationRepository.findWithUserAndPetAndTimeSlotById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        return ReservationDetailResponse.from(reservation);
    }

    // 예약 생성
    @Transactional
    public Long createReservation(ReservationCreateRequest request) {

        try {
            TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.TIME_SLOT_NOT_FOUND));

            timeSlot.validateReservable();

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            Pet pet = petRepository.findById(request.getPetId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

            Shop shop = shopRepository.findById(request.getShopId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));

            timeSlot.reserve();

            Reservation reservation = Reservation.create(
                    user, pet, shop, timeSlot, request.getDepositAmount()
            );

            Reservation saved = reservationRepository.save(reservation);

            return saved.getId();

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException(ErrorCode.TIME_SLOT_ALREADY_RESERVED);
        }
    }


    // 예약 확정
    @Transactional
    public Long confirmReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.confirm();

        return reservation.getId();
    }

    // 예약 취소
    @Transactional
    public Long cancelReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.cancel();

        return reservation.getId();
    }

}


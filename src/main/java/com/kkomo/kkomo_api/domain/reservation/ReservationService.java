package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.pet.Pet;
import com.kkomo.kkomo_api.domain.pet.PetRepository;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlot;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlotRepository;
import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.domain.user.UserRepository;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final ReservationRepository reservationRepository;

    // 예약 생성
    @Transactional
    public Long createReservation(Long userId, Long petId, Long timeSlotId, BigDecimal depositAmount) {

        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TIME_SLOT_NOT_FOUND));

        timeSlot.validateReservable();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

        Reservation reservation = Reservation.create(user, pet, timeSlot, depositAmount);

        reservationRepository.save(reservation);

        timeSlot.reserve();

        return reservation.getId();
    }

    // 예약 확정
    @Transactional
    public Long confirmReservation(Long reservationId) {

        // 1. 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        // 2. 상태 검증 ( 어떤 상태일 때 예약 확정이 가능할지 ), 상태 변경(예약 확정)
        reservation.confirm();

        // 3. 예약 id 리턴
        return reservation.getId();
    }

    // 예약 취소
    @Transactional
    public Long cancelReservation(Long reservationId) {

        // 1. 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        // 2. 예약 상태 검증, 상태 변경, TimeSlot 복구
        reservation.cancel();

        // 3. id 반환
        return reservation.getId();
    }

}


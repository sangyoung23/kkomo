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

    @Transactional
    public Long createReservation(Long userId, Long petId, Long timeSlotId, BigDecimal depositAmount) {

        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TIME_SLOT_NOT_FOUND));

        // 슬롯 예약 가능 여부는 슬롯이 판단
        timeSlot.validateReservable();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PET_NOT_FOUND));

        // 예약 생성 (PENDING 상태)
        Reservation reservation = Reservation.create(user, pet, timeSlot, depositAmount);

        reservationRepository.save(reservation);

        // 슬롯 점유
        timeSlot.reserve();

        return reservation.getId();
    }
}


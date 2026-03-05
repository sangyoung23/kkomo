package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.domain.pet.Pet;
import com.kkomo.kkomo_api.domain.pet.PetRepository;
import com.kkomo.kkomo_api.domain.reservation.dto.ReservationCreateRequest;
import com.kkomo.kkomo_api.domain.shop.Shop;
import com.kkomo.kkomo_api.domain.shop.ShopRepository;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlot;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlotRepository;
import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.domain.user.UserRepository;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock TimeSlotRepository timeSlotRepository;
    @Mock UserRepository userRepository;
    @Mock PetRepository petRepository;
    @Mock ShopRepository shopRepository;
    @Mock ReservationRepository reservationRepository;

    @InjectMocks
    ReservationService reservationService;

    private ReservationCreateRequest createReservationRequest() {
        return ReservationCreateRequest.builder()
                .userId(1L)
                .petId(2L)
                .shopId(3L)
                .timeSlotId(4L)
                .build();
    }

    // ===== createReservation() =====

    @Test
    @DisplayName("예약 생성 성공")
    void createReservation_success() {

        // given
        ReservationCreateRequest request = createReservationRequest();

        TimeSlot timeSlot = mock(TimeSlot.class);
        User user = mock(User.class);
        Pet pet = mock(Pet.class);
        Shop shop = mock(Shop.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(petRepository.findById(2L))
                .thenReturn(Optional.of(pet));
        when(shopRepository.findById(3L))
                .thenReturn(Optional.of(shop));
        when(timeSlotRepository.findById(4L))
                .thenReturn(Optional.of(timeSlot));

        Reservation reservation = mock(Reservation.class);
        when(reservation.getId()).thenReturn(10L);

        when(reservationRepository.save(any()))
                .thenReturn(reservation);

        // when
        Long result = reservationService.createReservation(request);

        // then
        assertThat(result).isEqualTo(10L);

        verify(timeSlot).validateReservable();
        verify(timeSlot).reserve();
        verify(reservationRepository).save(any());
    }

    @Test
    @DisplayName("타임슬롯이 존재하지 않으면 예외 발생")
    void createReservation_fail_timeSlotNotFound() {

        // given
        ReservationCreateRequest request = createReservationRequest();

        when(timeSlotRepository.findById(4L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                    reservationService.createReservation(request)
                )
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.TIME_SLOT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("유저가 존재하지 않으면 예외 발생")
    void createReservation_fail_userNotFound() {

        // given
        ReservationCreateRequest request = createReservationRequest();

        TimeSlot timeSlot = mock(TimeSlot.class);

        when(timeSlotRepository.findById(4L))
                .thenReturn(Optional.of(timeSlot));
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                reservationService.createReservation(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("펫이 존재하지 않으면 예외 발생")
    void createReservation_fail_petNotFound() {

        // given
        ReservationCreateRequest request = createReservationRequest();

        TimeSlot timeSlot = mock(TimeSlot.class);
        User user = mock(User.class);

        when(timeSlotRepository.findById(4L))
                .thenReturn(Optional.of(timeSlot));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(petRepository.findById(2L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                reservationService.createReservation(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PET_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("샵이 존재하지 않으면 예외 발생")
    void createReservation_fail_shopNotFound() {

        // given
        ReservationCreateRequest request = createReservationRequest();

        TimeSlot timeSlot = mock(TimeSlot.class);
        User user = mock(User.class);
        Pet pet = mock(Pet.class);

        when(timeSlotRepository.findById(4L))
                .thenReturn(Optional.of(timeSlot));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(petRepository.findById(2L))
                .thenReturn(Optional.of(pet));
        when(shopRepository.findById(3L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                reservationService.createReservation(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.SHOP_NOT_FOUND.getMessage());
    }

    // ===== confirmReservation() =====

//    @Test
//    @DisplayName("예약 확정 성공")
//    void confirmReservation_success() {
//
//        // given
//        Reservation reservation = mock(Reservation.class);
//
//        when(reservationRepository.findById(1L))
//                .thenReturn(Optional.of(reservation));
//        when(reservation.getId()).thenReturn(1L);
//
//        Long result = reservationService.confirmReservation(1L);
//
//        // when & then
//        assertThat(result).isEqualTo(1L);
//        verify(reservation).confirm();
//    }

//    @Test
//    @DisplayName("예약이 존재하지 않으면 예외 발생")
//    void confirmReservation_fail_reservationNotFound() {
//
//        // given
//        when(reservationRepository.findById(1L))
//                .thenReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() ->
//                reservationService.confirmReservation(1L)
//        )
//                .isInstanceOf(BusinessException.class)
//                .hasMessage(ErrorCode.RESERVATION_NOT_FOUND.getMessage());
//    }

    // ===== cancelReservation() =====

    @Test
    @DisplayName("예약 취소 성공")
    void cancelReservation_success() {

        // given
        Reservation reservation = mock(Reservation.class);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        // when
        reservationService.cancelReservation(1L, 2L);

        // then
        verify(reservation).cancel();
    }

    @Test
    @DisplayName("예약이 존재하지 않으면 예외 발생")
    void cancelReservation_fail_reservationNotFound() {

        // given
        when(reservationRepository.findById(1L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                reservationService.cancelReservation(1L, 2L)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.RESERVATION_NOT_FOUND.getMessage());
    }
}

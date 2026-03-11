package com.kkomo.kkomo_api.domain.review;

import com.kkomo.kkomo_api.domain.pet.Pet;
import com.kkomo.kkomo_api.domain.reservation.Reservation;
import com.kkomo.kkomo_api.domain.reservation.ReservationRepository;
import com.kkomo.kkomo_api.domain.reservation.ReservationStatus;
import com.kkomo.kkomo_api.domain.review.dto.ReviewCreateRequest;
import com.kkomo.kkomo_api.domain.review.dto.ReviewListResponse;
import com.kkomo.kkomo_api.domain.shop.ShopRepository;
import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ShopRepository shopRepository;

    @InjectMocks
    ReviewService reviewService;

    // ===== getReviews() =====

    @Test
    @DisplayName("리뷰 조회 성공")
    void getReviews_success() {

        // given
        Long shopId = 1L;

        User user = User.builder()
                .name("상용")
                .build();

        Pet pet = Pet.builder()
                .name("코코")
                .build();

        Reservation reservation1 = Reservation.builder()
                .user(user)
                .pet(pet)
                .status(ReservationStatus.COMPLETED)
                .build();

        Reservation reservation2 = Reservation.builder()
                .user(user)
                .pet(pet)
                .status(ReservationStatus.COMPLETED)
                .build();

        Review review1 = Review.create("좋아요", 5, reservation1);
        Review review2 = Review.create("괜찮아요", 3, reservation2);

        when(reviewRepository.findByReservationShopId(shopId))
                .thenReturn(List.of(review1, review2));

        // when
        ReviewListResponse result = reviewService.getReviews(shopId);

        // then
        assertThat(result.getReviewCount()).isEqualTo(2);
        assertThat(result.getAverageRating()).isEqualTo(4.0);
    }

    // ===== createReview() =====

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_success() {

        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .reservationId(1L)
                .content("좋아요")
                .rating(5)
                .build();

        Reservation reservation = mock(Reservation.class);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        // when
        reviewService.createReview(request);

        // then
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("예약이 존재하지 않으면 리뷰 생성 실패")
    void createReview_fail_reservationNotFound() {

        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .reservationId(1L)
                .content("좋아요")
                .rating(5)
                .build();

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                reviewService.createReview(request)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.RESERVATION_NOT_FOUND.getMessage());
    }
}
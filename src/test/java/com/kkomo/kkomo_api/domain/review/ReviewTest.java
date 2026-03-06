package com.kkomo.kkomo_api.domain.review;

import com.kkomo.kkomo_api.domain.reservation.Reservation;
import com.kkomo.kkomo_api.domain.reservation.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewTest {

    // ===== create() =====

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_success() {

        // given
        Reservation reservation = mock(Reservation.class);

        when(reservation.getStatus()).thenReturn(ReservationStatus.COMPLETED);


        // when
        Review review = Review.create("친절해요", 5, reservation);

        // then
        assertThat(review.getContent()).isEqualTo("친절해요");
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getReservation()).isEqualTo(reservation);
        verify(reservation).validateReviewable();
    }

}

package com.kkomo.kkomo_api.domain.review;

import com.kkomo.kkomo_api.domain.reservation.Reservation;
import com.kkomo.kkomo_api.domain.reservation.ReservationRepository;
import com.kkomo.kkomo_api.domain.review.dto.ReviewCreateRequest;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    // 리뷰 작성
    @Transactional
    public void createReview(ReviewCreateRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        Review review = Review.create(request.getContent(), request.getRating(), reservation);

        reviewRepository.save(review);
    }
}
